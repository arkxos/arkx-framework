package org.ark.framework.jaf.zhtml;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.ark.framework.jaf.MainFilter;

import com.rapidark.framework.Account;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.StringUtil;


/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlExecutor
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:54:48 
 * @version V1.0
 */
public class ZhtmlExecutor {
	public static final String CMD_DEFINETAG = "DEFINETAG";
	public static final String CMD_INITTAG = "INITTAG";
	public static final String CMD_PRINTLN = "PRINTLN";
	public static final String CMD_PRINT = "PRINT";
	public static final String CMD_EVALHOLDER = "EVALHOLDER";
	public static final String CMD_INVOKETAG = "INVOKETAG";
	public static final String CMD_IF = "IF";
	public static final String CMD_FOR = "FOR";
	public static final String CMD_ISSKIP = "ISSKIP";
	public static final String CMD_ISEND = "ISEND";
	public static final String CMD_ONENTER = "ONENTER";
	public static final String CMD_BEFOREBODY = "BEFOREBODY";
	public static final String CMD_AFTERBODY = "AFTERBODY";
	public static final String CMD_ONEXIT = "ONEXIT";
	public static final String CMD_RETURN = "RETURN";
	public static final String CMD_SCRIPT = "SCRIPT";
	public static final String CMD_ENDIF = "ENDIF";
	public static final String CMD_ENDFOR = "ENDFOR";
	public static final String CMD_ENDTAG = "ENDTAG";
	private String code;
	private String[][] commands;
	private Mapx<String, int[]> tagMap = new Mapx<String, int[]>();
	protected String fileName;
	protected String contentType;
	protected boolean sessionFlag;
	protected int commandStart;
	protected long lastModified;
	protected boolean fromJar;

	public ZhtmlExecutor(String code) {
		this.code = code;
		init();
	}

	private void init() {
		if (ObjectUtil.empty(this.code)) {
			throw new RuntimeException("Commands is empty!");
		}
		String[] lines = StringUtil.splitEx(this.code, "\n");
		if (lines.length == 0) {
			throw new RuntimeException("Commands is empty!");
		}
		this.commands = new String[lines.length][2];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int index = line.indexOf(':');
			String command = line.substring(0, index);
			String params = line.substring(index + 1);
			commands[i] = (new String[] { command, params });

			if (command.equals("DEFINETAG"))
				for (int j = i + 1; j < lines.length; j++)
					if (lines[j].startsWith("ENDTAG:")) {
						this.tagMap.put(params, new int[] { i + 1, j });
						break;
					}
		}
	}

	public void execute(ZhtmlPage page) throws ZhtmlRuntimeException {
		HttpSession session = page.getRequest().getSession(false);
		if ((session == null) && (this.sessionFlag)) {
			Account.UserData u = MainFilter.createUserData(page.getRequest());
			session = page.getRequest().getSession(true);
			u.setSessionID(session.getId());
			Account.setCurrent(u);
		}
		if (!this.sessionFlag) {
			page.getRequest().setAttribute("_ARK_NOSESSION", "true");
		}
		int i = this.commandStart;
		do
			i = executeOneLine(i, page);
		while ((i >= 0) && (i < this.commands.length));
	}

	public int executeOneLine(int i, ZhtmlPage page) throws ZhtmlRuntimeException {
		ZhtmlTag currentTag = page.getCurrentTag();
		if ((i < 0) || (i >= this.commands.length)) {
			return i;
		}
		if (i == this.commands.length) {
			return i + 1;
		}
		if ((this.commands[i] == null) || (this.commands[i][0] == null)) {
			return i + 1;
		}
		String command = this.commands[i][0];
		String params = this.commands[i][1];
		try {
			if (command.equals("PRINTLN")) {
				println(params, page);
				return i + 1;
			}
			if (command.equals("PRINT")) {
				print(params, page);
				return i + 1;
			}
			if (command.equals("EVALHOLDER")) {
				evalHolder(params, page);
				return i + 1;
			}
			if (command.equals("FOR"))
				return forCommand(i, params, page);
			if (command.equals("IF"))
				return ifCommand(i, params, page);
			if (command.equals("INVOKETAG")) {
				if (invokeTag(params, page)) {
					return i + 1;
				}
				return -1;
			}
			if (command.equals("ONEXIT")) {
				currentTag.onExit(page);
				return i + 1;
			}
			if (command.equals("AFTERBODY")) {
				currentTag.afterBody(page);
				return i + 1;
			}
			if (command.equals("BEFOREBODY")) {
				currentTag.beforeBody(page);
				return i + 1;
			}
			if (command.equals("ONENTER")) {
				currentTag.onEnter(page);
				return i + 1;
			}
			if (command.equals("RETURN"))
				return -1;
			if (command.equals("DEFINETAG")) {
				for (int j = i + 1; j < this.commands.length; j++) {
					if (this.commands[j][0].equals("ENDTAG"))
						return j + 1;
				}
			} else if (command.equals("INITTAG")) {
				String[] arr = StringUtil.splitEx(params, ",");
				String subCommand = arr[0];
				if (subCommand.equals("new")) {
					ZhtmlTag tag = TagLibLoader.getTag(arr[1], arr[2]).clone();
					tag.setOut(page.getPageContext().getOut());
					tag.getTagSupport().setPageContext(page.getPageContext());
					if (page.getCurrentTag() != null) {
						tag.setParent(page.getCurrentTag());
					}
					page.setCurrentTag(tag);
					currentTag = tag;
				} else if (subCommand.equals("setStartLineNo")) {
					page.getCurrentTag().setStartLineNo(Integer.parseInt(arr[1]));
				} else if (subCommand.equals("setAttribute")) {
					String attr = params.substring(params.indexOf(",", "setAttribute".length() + 2) + 1);
					page.getCurrentTag().setAttribute(arr[1], attr);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ZhtmlRuntimeException(e.getMessage());
		}
		return i + 1;
	}

	public void println(String params, ZhtmlPage page) throws IOException {
		page.getWriter().write(StringUtil.javaDecode(params));
		page.getWriter().write("\n");
	}

	public void print(String params, ZhtmlPage page) throws IOException {
		page.getWriter().write(StringUtil.javaDecode(params));
	}

	public void evalHolder(String params, ZhtmlPage page) throws IOException {
		print("${" + params + "}", page);
	}

	public int forCommand(int start, String params, ZhtmlPage page) throws ZhtmlRuntimeException {
		ZhtmlTag currentTag = page.getCurrentTag();
		int end = start + 1;
		int forCount = 0;
		for (int k = start + 1; k < this.commands.length; k++) {
			if (this.commands[k][0].equals("FOR")) {
				forCount++;
			}
			if (this.commands[k][0].equals("ENDFOR")) {
				if (forCount == 0) {
					end = k;
					break;
				}
				forCount--;
			}
		}
		TagSupport ts = currentTag.getTagSupport();
		while (true) {
			for (int k = start + 1; (k >= 0) && (k < end);)
				k = executeOneLine(k, page);
			try {
				if (ts.doAfterBody() == 2)
					continue;
				else {
					return end;
				}
			} catch (JspException e) {
				e.printStackTrace();
				throw new ZhtmlRuntimeException(e.getMessage());
			}
		}
	}

	public int ifCommand(int start, String params, ZhtmlPage page) throws ZhtmlRuntimeException {
		ZhtmlTag currentTag = page.getCurrentTag();
		String booleanCommand = params;
		boolean flag = false;
		try {
			if ((booleanCommand.equals("ISSKIP")) && (currentTag.getStartTagFlag() != 0))
				flag = true;
			else if ((booleanCommand.equals("ISEND")) && (currentTag.getTagSupport().doEndTag() == 5))
				return -1;
		} catch (JspException e) {
			e.printStackTrace();
			throw new ZhtmlRuntimeException(e.getMessage());
		}
		int end = start + 1;
		int ifCount = 0;
		for (; end < this.commands.length; end++) {
			if (this.commands[end][0].equals("IF")) {
				ifCount++;
			}
			if (this.commands[end][0].equals("ENDIF")) {
				if (ifCount == 0) {
					break;
				}
				ifCount--;
			}
		}
		if (flag) {
			for (int k = start + 1; (k >= 0) && (k < end);) {
				k = executeOneLine(k, page);
			}
		}
		return end;
	}

	public boolean invokeTag(String params, ZhtmlPage page) throws ZhtmlRuntimeException {
		int[] arr = (int[]) this.tagMap.get(params);
		if (arr == null) {
			throw new ZhtmlRuntimeException("Invoke tag failed:" + params);
		}
		int i = arr[0];
		do
			i = executeOneLine(i, page);
		while ((i >= 0) && (i < arr[1]));

		return i >= 0;
	}

	public String getFileName() {
		return this.fileName;
	}

	public long getLastModified() {
		return this.lastModified;
	}
}