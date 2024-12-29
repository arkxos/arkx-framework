package com.arkxos.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.tag.ArkTag;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.cosyui.util.TagUtil;
import com.rapidark.framework.Config;
import com.rapidark.framework.Current;
import com.rapidark.framework.FrameworkPlugin;

/**
 * 上传控件标签　
 * 
 */
public class UploaderTag extends ArkTag {
	private String id;
	private String url;
	private String name;

	private String barColor;

	private int width;

	private int height;

	private String allowedType;

	private int fileCount;

	private int fileMaxSize;

	private String fileName;// 用于编辑时显示文件

	@Override
	public String getTagName() {
		return "uploader";
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws TemplateRuntimeException {// 没有body时也要执行
		String content = getBody();
		if (StringUtil.isEmpty(content)) {
			content = "";
		}
		pageContext.getOut().write(getHtml(""));
		return EVAL_PAGE;
	}

	/**
	 * 便于在Java文件中调用
	 */
//	public String getHtml(String content) {
//		String FlashVars = "";
//		String srcSWF = Config.getContextPath() + "framework/components/ZUploader2.swf";
//		if (StringUtil.isEmpty(id)) {// 产生随机ID
//			id = TagUtil.getTagID(pageContext, "File");
//		}
//		if (StringUtil.isEmpty(name)) {
//			name = id;
//		}
//		if (StringUtil.isNotEmpty(allowedType)) {
//			FlashVars += "elemId=" + id;
//		}
//		if (StringUtil.isNotEmpty(allowedType)) {
//			FlashVars += "&fileType=" + allowedType;
//		}
//		if (StringUtil.isNotEmpty(fileName)) {
//			FlashVars += "&fileName=" + StringUtil.htmlEncode(fileName);
//		}
//		if (StringUtil.isNotEmpty(barColor)) {
//			FlashVars += "&barColor=" + barColor;
//		}
//		if (fileCount != 0) {
//			FlashVars += "&fileCount=" + fileCount;
//		}
//		if (fileMaxSize != 0) {
//			FlashVars += "&fileMaxSize=" + fileMaxSize;
//		}
//		if (width == 0) {
//			width = 250;
//		}
//		if (height == 0) {
//			height = 25;
//		}
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("<!--[if IE]>");
//		sb.append("<object id=\"" + id + "\" name=\"" + name + "\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"" + width
//				+ "\" height=\"" + height + "\" style=\"vertical-align:middle;\">\n");
//		sb.append("<param name=\"movie\" value=\"" + srcSWF + "\">\n");
//		sb.append("<param name=\"quality\" value=\"high\">\n");
//		sb.append("<param name=\"wmode\" value=\"transparent\">\n");
//		sb.append("<param name=\"allowScriptAccess\" value=\"always\">\n");
//		sb.append("<param name=\"FlashVars\" value=\"" + FlashVars + "\">\n");
//		sb.append("</object>");
//		sb.append("<![endif]-->\n");
//		sb.append("<!--[if !IE]>-->");
//		sb.append("<object id=\"" + id + "\" name=\"" + name + "\" type=\"application/x-shockwave-flash\" data=\"" + srcSWF + "\" width=\""
//				+ width + "\" height=\"" + height + "\" style=\"vertical-align:middle;\">\n");
//		sb.append("<param name=\"quality\" value=\"high\">\n");
//		sb.append("<param name=\"wmode\" value=\"transparent\">\n");
//		sb.append("<param name=\"allowScriptAccess\" value=\"always\">\n");
//		sb.append("<param name=\"FlashVars\" value=\"" + FlashVars + "\">\n");
//		sb.append("</object>");
//		sb.append("<!--<![endif]-->");
//
//		sb.append("<script>Ark.Uploader.checkVersion();</script>\n");
//		return sb.toString();
//	}
	public String getHtml(String content) {
		String attrs = "";
		if (StringUtil.isEmpty(this.id)) {
			this.id = TagUtil.getTagID(this.pageContext, "File");
		}
		if (StringUtil.isEmpty(this.name)) {
			this.name = this.id;
		}
		if (StringUtil.isNotEmpty(this.allowedType)) {
			attrs = attrs + " allowType=\"" + this.allowedType + "\"";
		}
		if (StringUtil.isNotEmpty(this.fileName)) {
			attrs = attrs + " fileName=\"" + StringUtil.htmlEncode(this.fileName) + "\"";
		}
		if (StringUtil.isNotEmpty(this.barColor)) {
			attrs = attrs + " barColor=\"" + this.barColor + "\"";
		}
		if (this.fileCount != 0) {
			attrs = attrs + " fileCount=\"" + this.fileCount + "\"";
		}
		if (this.fileMaxSize != 0) {
			attrs = attrs + " fileMaxSize=\"" + this.fileMaxSize + "\"";
		}
		attrs = attrs + " sessionid=\"" + Current.getRequest().getSessionID() + "\"";
		if (this.width == 0) {
			this.width = 250;
		}
		if (this.height == 0) {
			this.height = 25;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"" + this.id + "_outer\" class=\"uploader-outer\" style=\"width: " + this.width + "px\">");
		sb.append("    <div class=\"btns\">");
		sb.append("        <div id=\"" + this.id + "_statusBar\" class=\"uploader-statusBar\"></div>");
		sb.append("        <div id=\"" + this.id + "\"" + attrs + ">选择文件</div>");
		sb.append("        <button id=\"" + this.id + "_btn\" class=\"btn-uploader\">开始上传</button>");
		sb.append("    </div>");
		sb.append("    <ul id=\"" + this.id + "_list\" class=\"uploader-list\"></ul>");
		sb.append("</div>");

		sb.append("<script> new Ark.Uploader(");
		sb.append("'").append(this.id).append("'");
		if(!StringUtil.isEmpty(url)) {
			sb.append(",'").append(this.url).append("'");	
		}
		sb.append(");</script>");
		return sb.toString();
	}

	public String getFlashHtml(String content) {
		String FlashVars = "";
		String srcSWF = Config.getContextPath() + "framework/components/ZUploader2.swf";
		if (StringUtil.isEmpty(this.id)) {
			this.id = TagUtil.getTagID(this.pageContext, "File");
		}
		if (StringUtil.isEmpty(this.name)) {
			this.name = this.id;
		}
		if (StringUtil.isNotEmpty(this.allowedType)) {
			FlashVars = FlashVars + "elemId=" + this.id;
		}
		if (StringUtil.isNotEmpty(this.allowedType)) {
			FlashVars = FlashVars + "&fileType=" + this.allowedType;
		}
		if (StringUtil.isNotEmpty(this.fileName)) {
			FlashVars = FlashVars + "&fileName=" + StringUtil.htmlEncode(this.fileName);
		}
		if (StringUtil.isNotEmpty(this.barColor)) {
			FlashVars = FlashVars + "&barColor=" + this.barColor;
		}
		if (this.fileCount != 0) {
			FlashVars = FlashVars + "&fileCount=" + this.fileCount;
		}
		if (this.fileMaxSize != 0) {
			FlashVars = FlashVars + "&fileMaxSize=" + this.fileMaxSize;
		}
		if (this.width == 0) {
			this.width = 250;
		}
		if (this.height == 0) {
			this.height = 25;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<!--[if IE]>");
		sb.append("<object id=\"" + this.id + "\" name=\"" + this.name + "\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"" + this.width + "\" height=\""
				+ this.height + "\" style=\"vertical-align:middle;\">\n");
		sb.append("<param name=\"movie\" value=\"" + srcSWF + "\">\n");
		sb.append("<param name=\"quality\" value=\"high\">\n");
		sb.append("<param name=\"wmode\" value=\"transparent\">\n");
		sb.append("<param name=\"allowScriptAccess\" value=\"always\">\n");
		sb.append("<param name=\"FlashVars\" value=\"" + FlashVars + "\">\n");
		sb.append("</object>");
		sb.append("<![endif]-->\n");
		sb.append("<!--[if !IE]>-->");
		sb.append("<object id=\"" + this.id + "\" name=\"" + this.name + "\" type=\"application/x-shockwave-flash\" data=\"" + srcSWF + "\" width=\"" + this.width + "\" height=\""
				+ this.height + "\" style=\"vertical-align:middle;\">\n");
		sb.append("<param name=\"quality\" value=\"high\">\n");
		sb.append("<param name=\"wmode\" value=\"transparent\">\n");
		sb.append("<param name=\"allowScriptAccess\" value=\"always\">\n");
		sb.append("<param name=\"FlashVars\" value=\"" + FlashVars + "\">\n");
		sb.append("</object>");
		sb.append("<!--<![endif]-->");

		sb.append("<script>Ark.Uploader.checkVersion();</script>\n");
		return sb.toString();
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBarColor() {
		return barColor;
	}

	public void setBarColor(String barColor) {
		this.barColor = barColor;
	}

	public String getAllowType() {
		return allowedType;
	}

	public void setAllowType(String allowType) {
		allowedType = allowType;
	}
	/*
	 * 获取上传文件名
	 */
	public String getFileName() {
		return fileName;
	}
	/*
	 * 设置上传文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("url"));
		list.add(new TagAttr("allowType"));// 应废弃
		list.add(new TagAttr("allowedType"));
		list.add(new TagAttr("barcolor"));
		list.add(new TagAttr("fileCount", DataTypes.INTEGER.code()));
		list.add(new TagAttr("height", DataTypes.INTEGER.code()));
		list.add(new TagAttr("width", DataTypes.INTEGER.code()));
		list.add(new TagAttr("fileMaxSize", DataTypes.INTEGER.code()));
		list.add(new TagAttr("fileName"));
		list.add(new TagAttr("name"));
		return list;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public int getFileMaxSize() {
		return fileMaxSize;
	}

	public void setFileMaxSize(int fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.UploaderTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	public String getAllowedType() {
		return allowedType;
	}

	public void setAllowedType(String allowedType) {
		this.allowedType = allowedType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
