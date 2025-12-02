package io.arkx.framework.cosyui.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * 模板执行器
 */
public class TemplateExecutor implements Serializable {

	protected HashMap<String, Long> includeFiles = new HashMap<>();

	private static final long serialVersionUID = 1L;

	protected ITemplateCommand[] commands;

	protected String fileName; // 由Compiler赋值，有可能为空

	protected String contentType;

	protected boolean sessionFlag;

	protected long lastModified;

	protected long lastCheckTime;

	protected boolean fromJar;// 是否从jar文件中读取

	protected ITemplateManagerContext managerContext;

	protected Treex<String, AbstractTag> tree = new Treex<>();

	protected Mapx<String, Object> attributes = new Mapx<>();

	public void init(List<ITemplateCommand> commandList) {
		commands = new ITemplateCommand[commandList.size()];
		commands = commandList.toArray(commands);
	}

	public void execute(AbstractExecuteContext context) throws TemplateRuntimeException {
		context.setExecutor(this);
		for (ITemplateCommand command : commands) {
			try {
				// System.out.println(command);
				int executeResult = command.execute(context);
				// System.out.println("==========execute command:" + command);
				// System.out.println(context.getOut().toString());
				if (executeResult == AbstractTag.SKIP_PAGE) {
					break;
				}
			}
			catch (TemplateRuntimeException t) {
				t.printStackTrace();
				// throw t;
				context.getOut().write(t.getMessage());
			}
		}
	}

	public String getFileName() {
		return fileName;
	}

	public long getLastModified() {
		return lastModified;
	}

	public boolean isFromJar() {
		return fromJar;
	}

	public void setFromJar(boolean fromJar) {
		this.fromJar = fromJar;
	}

	public boolean isSessionFlag() {
		return sessionFlag;
	}

	public ITemplateManagerContext getExecuteContext() {
		return managerContext;
	}

	public long getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public Treex<String, AbstractTag> getTagTree() {
		return tree;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public TemplateExecutor(ITemplateManagerContext executeContext) {
		managerContext = executeContext;
		lastCheckTime = System.currentTimeMillis();
	}

	public HashMap<String, Long> getIncludeFiles() {
		return includeFiles;
	}

	public Mapx<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Mapx<String, Object> attributes) {
		this.attributes = attributes;
	}

	public ITemplateCommand[] getCommands() {
		return commands;
	}

	public void setCommands(ITemplateCommand[] commands) {
		this.commands = commands;
	}

	public ITemplateManagerContext getManagerContext() {
		return managerContext;
	}

}
