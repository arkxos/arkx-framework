package org.ark.framework.jaf.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.ark.framework.jaf.ParamManager;
import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.controls.ChildTab;


/**
 * @class org.ark.framework.jaf.tag.ChildTabTag
 * <h2>标签项</h2>
 * 
 * @author Darkness
 * @date 2012-11-19 下午03:12:32 
 * @version V1.0
 */
public class ChildTabTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 * @property id
	 * @type {String}
	 */
	private String id;
	
	/**
	 * 选项卡点击执行函数
	 * @property onClick
	 * @type {Function}
	 */
	private String onClick;
	
	/**
	 * 选项卡点击后执行函数
	 * @property afterClick
	 * @type {Function}
	 */
	private String afterClick;
	
	/**
	 * 选项卡页面
	 * @property src
	 * @type {String}
	 */
	private String src;
	
	/**
	 * 是否默认选中
	 * @property selected
	 * @type {boolean}
	 */
	private boolean selected;
	
	/**
	 * 是否禁用
	 * @property disabled
	 * @type {boolean}
	 */
	private boolean disabled;
	
	/**
	 * 是否显示
	 * @property visible
	 * @type {boolean}
	 */
	private boolean visible;
	
	/**
	 * 是否懒加载
	 * @property lazy
	 * @type {boolean}
	 */
	private boolean lazy;
	
	/**
	 * 显示类型，默认为iframe
	 * @property type(div/iframe)
	 * @type {String}
	 */
	private String type = "iframe";
	
	/**
	 * 小图标，如：“../../Icons/icon018a1.png”
	 * @property img
	 * @type {String}
	 */
	private String img;
	
	/**
	 * 标题
	 * @property title
	 * @type {String}
	 */
	private String title;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.id = null;
		this.onClick = null;
		this.afterClick = null;
		this.src = null;
		this.selected = false;
		this.disabled = false;
		this.visible = true;
		this.lazy = false;
	}

	public int doAfterBody() throws JspException {
		
		String content = getBodyContent().getString();
		
		try {
			ChildTab childTab = new ChildTab(content);
			
			childTab.setId(id);
			childTab.setTitle(title);
			childTab.setImg(img);
			childTab.setDisplayType(getType());
			childTab.setOnClick(onClick);
			childTab.setAfterClick(afterClick);
			
			Map<String, String> paramValues = new HashMap<String, String>();
			List<String> params = ParamManager.extractParam(this.src);
			for (String param : params) {
				PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
				String srcValue = context.eval(new PlaceHolder(param)) + "";
				paramValues.put(param, srcValue);
			}
			this.src = ParamManager.replaceParam(this.src, paramValues);
			
			childTab.setSrc(this.src);
			childTab.setSelected(selected);
			childTab.setDisabled(disabled);
			childTab.setVisible(visible);
			childTab.setLazy(lazy);
			
			String html = childTab.getHtml();
			getPreviousOut().print(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOnClick() {
		return this.onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getAfterClick() {
		return this.afterClick;
	}

	public void setAfterClick(String afterClick) {
		this.afterClick = afterClick;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getSrc() {
		return this.src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}