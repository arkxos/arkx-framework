package com.arkxos.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.FrameworkPlugin;
import io.arkx.framework.commons.collection.DataTypes;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * For循环标签。<br>
 * 用于指定步长的递增循环,from属性指明循环变量的起始值，to属性指明循环变量的结束值，step属性指明循环的步长。<br>
 * 注意：在循环体内可以获取到${i}用于表明循环变量的当前值，${first}用来表明是不是第一次循环，${last}用来表明是否是最后一次循环
 * 
 */
public class ForTag extends ArkTag {
	int from;
	int to;
	int step;
	int pos;

	@Override
	public String getTagName() {
		return "for";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		if (step == 0) {
			step = 1;
		}
		if (from == to || step > 0 && from > to || step < 0 && from < to) {
			return SKIP_BODY;
		} else {
			pos = from;
			context.addDataVariable("i", pos);
			context.addDataVariable("first", true);
			if (step > 0 && pos + step == to || step < 0 && pos - step == to) {
				context.addDataVariable("last", true);
			} else {
				context.addDataVariable("last", false);
			}
			return EVAL_BODY_INCLUDE;
		}
	}

	@Override
	public int doAfterBody() throws TemplateRuntimeException {
		if (this.variables.containsKey("_ARK_BREAK_TAG")) {
			return SKIP_BODY;
		}
		pos += step;
		if (step > 0 && pos < to || step < 0 && pos > to) {
			context.addDataVariable("i", pos);
			context.addDataVariable("first", false);
			if (step > 0 && pos + step == to || step < 0 && pos - step == to) {
				context.addDataVariable("last", true);
			}
			return EVAL_BODY_AGAIN;
		} else {
			return SKIP_BODY;
		}
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("from", true, DataTypes.INTEGER.code(), "@{Framework.CycleFrom}"));
		list.add(new TagAttr("to", true, DataTypes.INTEGER.code(), "@{Framework.CycleEnd}"));
		list.add(new TagAttr("step", false, DataTypes.INTEGER.code(), "@{Framework.CycleStep}"));
		return list;
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public String getDescription() {
		return "@{Framework.ZForTagDescription}";
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.ZForTagName}";
	}

}
