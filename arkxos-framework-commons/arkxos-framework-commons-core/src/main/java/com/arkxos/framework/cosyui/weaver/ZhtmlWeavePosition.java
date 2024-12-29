package com.arkxos.framework.cosyui.weaver;

import com.arkxos.framework.commons.util.RegexParser;

/**
 * Zhtml织入位置
 * 
 */
enum ZhtmlWeavePosition {

	/**
	 * &lt;head&gt;标签结束之前
	 */
	BeforeHeadEnd,
	/**
	 * &lt;head&gt;标签开始之后
	 */
	AfterHeadStart,
	/**
	 * &lt;body&gt;标签结束之前
	 */
	BeforeBodyEnd,
	/**
	 * &lt;body&gt;标签开始之后
	 */
	AfterBodyStart,
	/**
	 * &lt;html&gt;标签结束之前
	 */
	BeforeHtmlEnd,
	/**
	 * 文档开始之前
	 */
	BeforeDocumentStart,
	/**
	 * 文档结束之后
	 */
	AfterDocumentEnd;

	static RegexParser Regex_HeadEnd = new RegexParser("</head>", true);
	static RegexParser Regex_BodyEnd = new RegexParser("</body>", true);
	static RegexParser Regex_HtmlEnd = new RegexParser("</html>", true);
	static RegexParser Regex_HeadStart = new RegexParser("<head>", true);
	static RegexParser Regex_BodyStart = new RegexParser("<body>", true);
	static RegexParser Regex_HtmlStart = new RegexParser("<html>", true);

}