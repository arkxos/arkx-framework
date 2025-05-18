package org.ark.framework.jaf.zhtml;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arkxos.framework.commons.collection.tree.TreeNode;
import org.ark.framework.jaf.html.HtmlElement;

import com.arkxos.framework.Config;
import com.arkxos.framework.commons.collection.CaseIgnoreMapx;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.collection.tree.Treex;
import com.arkxos.framework.commons.util.Errorx;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.StringFormat;


/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlParser
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:55:41 
 * @version V1.0
 */
public class ZhtmlParser {
	private String fileName;
	private String content;
	private Treex<String, ZhtmlFragment> tree;
	private TreeNode<String, ZhtmlFragment> currentParent;
	private Mapx<String, String> prefixToURIMap = new Mapx();
	private boolean sessionFlag = true;
	private String contentType = null;
	private static final String ScriptStart = "<%";
	private static final String ScriptEnd = "%>";
	public static final Pattern PInclude = Pattern.compile("\\<\\%\\s*@\\s*include\\s+file\\=\\\"(.*?)\\\".*?\\%\\>", 34);

	public ZhtmlParser() {
	}

	public ZhtmlParser(String templateFileName) {
		setFileName(templateFileName);
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void parseDirective(String str) throws NotPrecompileException {
		str = str.trim();
		if (!str.startsWith("@")) {
			return;
		}
		str = str.substring(1).trim();
		if (str.indexOf(" ") < 0) {
			return;
		}
		String command = str.substring(0, str.indexOf(" "));
		str = str.substring(str.indexOf(" "));
		Mapx attrs = HtmlElement.parseAttr(str);
		if (command.equalsIgnoreCase("taglib")) {
			String url = attrs.getString("uri");
			String prefix = attrs.getString("prefix");
			this.prefixToURIMap.put(prefix, url);
		} else if (command.equalsIgnoreCase("page")) {
			this.sessionFlag = (!"false".equals(attrs.getString("session")));
			this.contentType = attrs.getString("contenttype");
		} else if (!command.equalsIgnoreCase("import")) {
			if (!command.equalsIgnoreCase("include")) {
				throw new NotPrecompileException("Compile failed,zhtml file contains script!");
			}
		}
	}

	private void include() {
		Matcher m = PInclude.matcher(this.content);
		int lastIndex = 0;
		StringBuilder sb = new StringBuilder();
		while (m.find(lastIndex)) {
			String current = this.fileName.indexOf("/") > 0 ? this.fileName.substring(0, this.fileName.lastIndexOf("/")) : "";
			String file = m.group(1);
			if (file.startsWith("/")) {
				file = Config.getContextRealPath() + file;
			} else {
				while (true) {
					if (file.startsWith("./")) {
						file = file.substring(2);
						continue;
					}
					if (!file.startsWith("../"))
						break;
					current = current.indexOf("/") > 0 ? current.substring(0, current.lastIndexOf("/")) : "";
					file = file.substring(3);
				}

				file = current + "/" + file;
			}
			
			file = FileUtil.normalizePath(file);
			String txt = null;
			if (new File(file).exists()) {
				txt = FileUtil.readText(file);
			} else if (new File(Config.getContextRealPath() + file).exists()) {
				txt = FileUtil.readText(Config.getContextRealPath() + file);
			} else {
				file = m.group(1);
				if (file.startsWith("/")) {
					file = file.substring(1);
				}
				txt = ZhtmlManager.getSourceInJar(file);
			}
			sb.append(this.content.substring(lastIndex, m.start()));
			if (txt != null)
				sb.append(txt);
			else {
				sb.append("#Include file not found:" + file);
			}
			lastIndex = m.end();
		}
		sb.append(this.content.substring(lastIndex));
		this.content = sb.toString();
		m = PInclude.matcher(this.content);
		if (m.find())
			include();
	}

	public void parse() throws ZhtmlCompileException, NotPrecompileException {
		if (this.tree != null) {
			return;
		}
		if (this.content == null) {
			throw new ZhtmlCompileException("Zhtml content cann't be empty!");
		}
		include();
		this.content = this.content.trim();
		char[] cs = this.content.toCharArray();
		int currentLineNo = 1;
		int holderStartIndex = -1;
		int htmlStartIndex = 0;
		int htmlStartLineNo = 0;
		int scriptStartIndex = -1;
		int scriptStartLineNo = -1;
		this.tree = new Treex();
		this.currentParent = this.tree.getRoot();
		String lowerTemplate = this.content.toLowerCase();
		char c;
		for (int i = 0; i < cs.length; i++) {
			c = cs[i];
			if (c == '\n') {
				currentLineNo++;
			}
			if (scriptStartIndex >= 0) {
				if ((c != '>') || (this.content.indexOf("%>", i - "%>".length() + 1) != i - "%>".length() + 1))
					continue;
				ZhtmlFragment tf = new ZhtmlFragment();
				tf.Type = 4;
				tf.FragmentText = this.content.substring(scriptStartIndex + "<%".length(), i - "%>".length() + 1);
				tf.StartLineNo = scriptStartLineNo;
				this.currentParent.addChildByValue(tf);
				htmlStartIndex = i + 1;
				htmlStartLineNo = currentLineNo;
				scriptStartIndex = -1;
				parseDirective(tf.FragmentText);
			} else if ((c == '$') && (i < cs.length - 1) && (cs[(i + 1)] == '{')) {
				if (holderStartIndex >= 0) {
					Errorx.addMessage("Error on line " + currentLineNo + ":placeholder not end!");
					htmlStartIndex = holderStartIndex;
					htmlStartLineNo = currentLineNo;
				}
				if ((htmlStartIndex != -1) && (htmlStartIndex != i)) {
					ZhtmlFragment tf = new ZhtmlFragment();
					tf.Type = 1;
					tf.FragmentText = this.content.substring(htmlStartIndex, i);
					tf.StartLineNo = htmlStartLineNo;
					this.currentParent.addChildByValue(tf);
					htmlStartIndex = -1;
				}
				holderStartIndex = i;
			} else if ((c == '}') || (c == '\n')) {
				if (holderStartIndex >= 0) {
					if ((c == '}') && (holderStartIndex + 2 < i)) {
						ZhtmlFragment tf = new ZhtmlFragment();
						tf.Type = 3;
						tf.FragmentText = this.content.substring(holderStartIndex + 2, i);
						tf.StartLineNo = currentLineNo;
						tf.StartCharIndex = holderStartIndex;
						tf.EndCharIndex = i;
						this.currentParent.addChildByValue(tf);
						htmlStartIndex = i + 1;
						htmlStartLineNo = currentLineNo;
					} else {
						Errorx.addMessage("Error on line " + currentLineNo + ":placeholder not end!");
						htmlStartIndex = holderStartIndex;
						htmlStartLineNo = currentLineNo;
					}
				}
				holderStartIndex = -1;
			} else {
				if ((c != '<') || (i >= cs.length - 1))
					continue;
				if ((cs[(i + 1)] == '!') && (lowerTemplate.indexOf("<!--", i) == i) && (lowerTemplate.indexOf("<%", i) != i)) {
					int end = lowerTemplate.indexOf("-->", i);
					if (end < 0) {
						continue;
					}
					for (int k = i; k < end; k++) {
						if (cs[k] == '\n') {
							currentLineNo++;
						}
					}
					i = end + 2;
				} else {
					int index = this.content.indexOf(":", i);
					int index2 = this.content.indexOf(">", i) + 1;
					boolean tagStartFlag = true;
					if (index > index2) {
						tagStartFlag = false;
					}
					if (tagStartFlag) {
						if (isRegisteredTagStart(lowerTemplate, i)) {
							int tagEnd = getTagEnd(cs, i + 1);
							if (tagEnd < 0) {
								Errorx.addError("Error on line " + currentLineNo + ":tag no end!");
								return;
							}
							if ((htmlStartIndex != -1) && (htmlStartIndex != i)) {
								ZhtmlFragment tf = new ZhtmlFragment();
								tf.Type = 1;
								tf.FragmentText = this.content.substring(htmlStartIndex, i);
								tf.StartLineNo = htmlStartLineNo;
								this.currentParent.addChildByValue(tf);
								htmlStartIndex = -1;
							}
							String tag = this.content.substring(i + 1, tagEnd).trim();
							ZhtmlFragment tf = new ZhtmlFragment();
							if (tag.endsWith("/")) {
								tf.StartLineNo = currentLineNo;
								tf.Type = 2;
								tf.StartCharIndex = i;
								tf.EndCharIndex = tagEnd;
								tf.FragmentText = null;
								this.currentParent.addChildByValue(tf);
							} else {
								tf.StartLineNo = currentLineNo;
								tf.Type = 2;
								tf.StartCharIndex = i;
								TreeNode tn = this.currentParent.addChildByValue(tf);
								this.currentParent = tn;
							}
							parseTagAttributes(tf, tag);
							for (int k = i; k < tagEnd; k++) {
								if (cs[k] == '\n') {
									currentLineNo++;
								}
							}
							i = tagEnd;
							htmlStartIndex = tagEnd + 1;
							continue;
						}
						if ((cs[(i + 1)] == '/') && (isRegisteredTagEnd(lowerTemplate, i))) {
							String tagEnd = this.content.substring(i, this.content.indexOf(">", i) + 1);
							ZhtmlFragment tf = (ZhtmlFragment) this.currentParent.getValue();
							if (tf == null) {
								Errorx.addError("Error on line " + currentLineNo + ":" + tagEnd + " no start!");
								return;
							}
							String prefix = tagEnd.substring(2, tagEnd.indexOf(":"));
							String tagName = tagEnd.substring(tagEnd.indexOf(":") + 1, tagEnd.length() - 1);
							if ((!prefix.equalsIgnoreCase(tf.TagPrefix)) || (!tagName.equalsIgnoreCase(tf.TagName))) {
								Errorx.addError("Error on line " + currentLineNo + ":" + tagEnd + " cann't match <" + tf.TagPrefix + ":" + tf.TagName + ">");
								return;
							}
							if ((htmlStartIndex != -1) && (htmlStartIndex != i)) {
								ZhtmlFragment tf2 = new ZhtmlFragment();
								tf2.Type = 1;
								tf2.FragmentText = this.content.substring(htmlStartIndex, i);
								tf2.StartLineNo = htmlStartLineNo;
								this.currentParent.addChildByValue(tf2);
								htmlStartIndex = -1;
							}
							tf.FragmentText = this.content.substring(getTagEnd(cs, tf.StartCharIndex + 1) + 1, i);
							int end = this.content.indexOf('>', i);
							for (int k = i; k < end; k++) {
								if (cs[k] == '\n') {
									currentLineNo++;
								}
							}
							tf.EndCharIndex = (i = end);
							this.currentParent = this.currentParent.getParent();
							htmlStartIndex = i + 1;
							htmlStartLineNo = currentLineNo;
							continue;
						}
					}
					if ((this.content.indexOf("<%", i) != i) || (htmlStartIndex == -1))
						continue;
					if (htmlStartIndex != i) {
						ZhtmlFragment tf = new ZhtmlFragment();
						tf.Type = 1;
						tf.FragmentText = this.content.substring(htmlStartIndex, i);
						tf.StartLineNo = htmlStartLineNo;
						this.currentParent.addChildByValue(tf);
						htmlStartIndex = -1;
						scriptStartIndex = i;
						scriptStartLineNo = currentLineNo;
					} else {
						scriptStartIndex = i;
						scriptStartLineNo = currentLineNo;
					}
				}
			}

		}

		if ((htmlStartIndex != -1) && (htmlStartIndex != cs.length - 1)) {
			ZhtmlFragment tf = new ZhtmlFragment();
			tf.Type = 1;
			tf.FragmentText = this.content.substring(htmlStartIndex);
			tf.StartLineNo = htmlStartLineNo;
			this.currentParent.addChildByValue(tf);
			htmlStartIndex = -1;
		}
		for (TreeNode tn : this.tree.iterator()) {
			ZhtmlFragment tf = (ZhtmlFragment) tn.getValue();
			if ((tf == null) || (tf.Type != 2) || (tf.EndCharIndex > 0))
				continue;
			Errorx.addError("Error on line " + tf.StartLineNo + ": <" + tf.TagPrefix + ":" + tf.TagName + "> no end");
			return;
		}
	}

	public boolean isRegisteredTagStart(String lowerTemplate, int index) {
		for (TagLibLoader.TagLib lib : TagLibLoader.load().valueArray()) {
			for (String prefix : this.prefixToURIMap.keyArray()) {
				String uri = (String) this.prefixToURIMap.get(prefix);
				if (lib.URI.equals(uri)) {
					for (ZhtmlTag tag : lib.Tags.valueArray()) {
						if (lowerTemplate.indexOf("<" + prefix + ":" + tag.TagName.toLowerCase(), index) == index) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isRegisteredTagEnd(String lowerTemplate, int index) {
		for (TagLibLoader.TagLib lib : TagLibLoader.load().valueArray()) {
			for (String prefix : this.prefixToURIMap.keyArray()) {
				String uri = (String) this.prefixToURIMap.get(prefix);
				if (lib.URI.equals(uri)) {
					for (ZhtmlTag tag : lib.Tags.valueArray()) {
						if (lowerTemplate.indexOf("</" + prefix + ":" + tag.TagName.toLowerCase(), index) == index) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public int getTagEnd(char[] cs, int start) {
		char lastStringChar = '\000';
		for (int i = start; i < cs.length; i++) {
			char c = cs[i];
			if ((c == '"') || (c == '\'')) {
				if ((i > 0) && (cs[(i - 1)] == '\\')) {
					continue;
				}
				if (lastStringChar == c)
					lastStringChar = '\000';
				else if (lastStringChar == 0) {
					lastStringChar = c;
				}
			}
			if ((c == '>') && (lastStringChar == 0)) {
				return i;
			}
			if ((c == '<') && (lastStringChar == 0)) {
				return -1;
			}
		}
		return -1;
	}

	public ZhtmlTag getTag(String prefix, String tagName) {
		String uri = (String) this.prefixToURIMap.get(prefix);
		if (uri == null) {
			return null;
		}
		TagLibLoader.TagLib lib = (TagLibLoader.TagLib) TagLibLoader.load().get(uri);
		if (lib == null) {
			return null;
		}
		ZhtmlTag tag = (ZhtmlTag) lib.Tags.get(tagName);
		return tag;
	}

	public void parseTagAttributes(ZhtmlFragment tf, String tagHTML) throws ZhtmlCompileException {
		String prefix = tagHTML.substring(0, tagHTML.indexOf(":")).trim();
		int nameEnd = -1;
		for (int i = prefix.length(); i < tagHTML.length(); i++) {
			if (Character.isWhitespace(tagHTML.charAt(i))) {
				nameEnd = i;
				break;
			}
		}
		String tagName = null;
		CaseIgnoreMapx map = new CaseIgnoreMapx();
		if (nameEnd > 0) {
			tagName = tagHTML.substring(tagHTML.indexOf(":") + 1, nameEnd).trim();
			tagHTML = tagHTML.substring(nameEnd + 1).trim();
			ZhtmlTag tag = getTag(prefix, tagName);
			if (tag == null) {
				String message = StringFormat.format("Error on line ?:<?:?> not registered.", new Object[] { Integer.valueOf(tf.StartLineNo), prefix, tagName });
				Errorx.addError(message);
				throw new ZhtmlCompileException(message);
			}
			if (tagHTML.endsWith("/")) {
				tagHTML = tagHTML.substring(0, tagHTML.length() - 1).trim();
			}
			tagHTML = tagHTML.replaceAll("\\s+", " ");
			char lastStringChar = '\000';
			int nameStartIndex = 0;
			int valueStartIndex = -1;
			String key = null;
			char[] cs = tagHTML.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				char c = cs[i];
				if ((c == '=') && (lastStringChar == 0)) {
					key = tagHTML.substring(nameStartIndex, i);
					if (!tag.hasAttribute(key)) {
						String message = StringFormat.format("Error on line ?:<?:?> no attribute ?.", new Object[] { Integer.valueOf(tf.StartLineNo), prefix, tagName, key });
						Errorx.addError(message);
						throw new ZhtmlCompileException(message);
					}
					nameStartIndex = 0;
				}
				if ((c == ' ') && (lastStringChar == 0)) {
					nameStartIndex = i + 1;
				}
				if (((c != '"') && (c != '\'')) || ((i > 0) && (cs[(i - 1)] == '\\'))) {
					continue;
				}
				if (lastStringChar == c) {
					lastStringChar = '\000';
					map.put(key, tagHTML.substring(valueStartIndex, i));
				} else if (lastStringChar != '"') {
					lastStringChar = c;
					valueStartIndex = i + 1;
				}
			}
		} else {
			tagName = tagHTML.substring(tagHTML.indexOf(":") + 1).trim();
		}
		tf.TagPrefix = prefix;
		tf.TagName = tagName;
		tf.Attributes = map;
	}

	public Treex<String, ZhtmlFragment> getTree() {
		return this.tree;
	}

	public static void main(String[] args) {
	}

	public boolean isSessionFlag() {
		return this.sessionFlag;
	}

	public String getContentType() {
		return this.contentType;
	}
}