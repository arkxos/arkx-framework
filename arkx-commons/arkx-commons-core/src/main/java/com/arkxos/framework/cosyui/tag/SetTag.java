package com.arkxos.framework.cosyui.tag;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.FrameworkPlugin;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.Primitives;
import com.arkxos.framework.core.bean.BeanUtil;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.json.JSON;

/**
 * 变量定义/置值标签，用于在模板中定义和修改变量。<br>
 * 如果本标签没有父标签，则定义的变量在整个模板范围内有效。<br>
 * 如果本标签外面有父标签，则定义的变量只在父标签之内、本标签之后的范围内有效。
 * 
 */
public class SetTag extends ArkTag {
	String var;
	Object value;
	String type;
	String json;

	@Override
	public String getTagName() {
		return "set";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int doStartTag() throws TemplateRuntimeException {
		try {
			Object v = null;
			if (this.value != null) {
				v = this.value;
				if (this.type != null) {
					if (this.type.equals("long")) {
						v = Long.valueOf(Primitives.getLong(v));
					}
					if (this.type.equals("double")) {
						v = Double.valueOf(Primitives.getDouble(v));
					}
					if (this.type.equals("boolean")) {
						v = Boolean.valueOf(Primitives.getBoolean(v));
					}
					if (this.type.equals("String")) {
						v = v.toString();
					}
				}
			} else if (json != null) {
				v = JSON.parse(json);
			}
			if (var.endsWith("]")) {
				String name = var.substring(0, var.lastIndexOf('[')).trim();
				Object obj = context.evalExpression("${" + name + "}");
				if (obj != null) {
					String ref = var.substring(var.lastIndexOf('[') + 1, var.length() - 1).trim();
					if (NumberUtil.isInt(ref)) {// 下标访问
						int i = Integer.parseInt(ref);
						if (obj.getClass().isArray() && Array.getLength(obj) > i) {
							Array.set(obj, Integer.parseInt(ref), v);
						} else if (obj instanceof List) {
							List list = (List) obj;
							while (list.size() <= i) {
								list.add(null);
							}
							list.set(i, v);
						}
					} else {// 字段名访问
						if (obj instanceof Map) {
							((Map) obj).put(ref, v);
						} else {
							BeanUtil.set(obj, ref, v);
						}
					}
				}
			} else if (var.indexOf(".") > 0) {
				Object obj = context.evalExpression("${" + var.substring(0, var.lastIndexOf('.')) + "}");
				if (obj != null) {
					String ref = var.substring(var.lastIndexOf(".") + 1);
					if (obj instanceof Map) {
						((Map) obj).put(ref, v);
					} else {
						BeanUtil.set(obj, ref, v);
					}
				}
			} else {// 新定义的变量
				pageContext.addRootVariable(var, v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("var", DataTypes.STRING.code(), "@{Framework.VarTag.Name}"));
		list.add(new TagAttr("value", DataTypes.STRING.code(), "@{Framework.VarTag.Value}"));
		list.add(new TagAttr("json", DataTypes.STRING.code(), "@{Framework.VarTag.JsonValue}"));
		Mapx<String, String> types = new Mapx<>();
	    types.put("int", "@{Framework.Int}");
	    types.put("long", "@{Framework.Long}");
	    types.put("float", "@{Framework.Float}");
	    types.put("double", "@{Framework.Double}");
	    types.put("String", "@{Framework.String}");
	    types.put("boolean", "@{Framework.Boolean}");
	    types.put("Object", "@{Framework.Object}");
	    list.add(new TagAttr("type", types, "@{Framework.VarTag.Type}"));
	    return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getDescription() {
		return "@{Framework.ZVarTagDescription}";//return "@{Framework.ZV-arTagDescription}";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.ZVarTagName}";
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
