package org.ark.framework.jaf.zhtml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import com.arkxos.framework.commons.collection.tree.TreeNode;
import com.arkxos.framework.commons.collection.tree.Treex;
import com.arkxos.framework.commons.util.Errorx;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.StringUtil;


/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlCompiler
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:54:30 
 * @version V1.0
 */
public class ZhtmlCompiler {
	private int VarID = 0;
	private String fileName;
	private long lastModified;
	private ZhtmlExecutor executor;
	private ArrayList<String> methodList = new ArrayList<String>();
	private ArrayList<String> commandList = new ArrayList<String>();
	private int commandStart;
	private ZhtmlParser parser = new ZhtmlParser();

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void compile(String JspFileName) throws FileNotFoundException, ZhtmlCompileException, NotPrecompileException {
		this.fileName = FileUtil.normalizePath(JspFileName);
		if (!new File(JspFileName).exists()) {
			throw new FileNotFoundException("File not found:" + JspFileName);
		}
		this.lastModified = new File(this.fileName).lastModified();
		compileSource(FileUtil.readText(this.fileName));
	}

	public void compileSource(String source) throws ZhtmlCompileException, ZhtmlCompileException, NotPrecompileException {
		long start = System.currentTimeMillis();
		this.parser.setContent(source);
		this.parser.setFileName(this.fileName);
		this.parser.parse();
		if (Errorx.hasError()) {
			throw new ZhtmlCompileException(Errorx.getAllMessage());
		}
		if (this.lastModified == 0L) {
			this.lastModified = System.currentTimeMillis();
		}

		Treex tree = this.parser.getTree();
		compile(tree);
		if (Errorx.hasError()) {
			throw new ZhtmlCompileException(Errorx.getAllMessage());
		}
		this.executor = new ZhtmlExecutor(StringUtil.join(this.commandList, "\n"));
		this.executor.fileName = this.fileName;
		this.executor.commandStart = this.commandStart;
		this.executor.lastModified = this.lastModified;
		this.executor.sessionFlag = this.parser.isSessionFlag();
		this.executor.contentType = this.parser.getContentType();

		LogUtil.info("Compile " + this.fileName + " cost " + (System.currentTimeMillis() - start) + " ms.");
	}

	public void compile(Treex<String, ZhtmlFragment> tree) {
		if (Errorx.hasError()) {
			return;
		}
		this.VarID = 0;
		this.commandStart = this.commandList.size();

		ArrayList list = tree.getRoot().getChildren();
		for (int i = 0; i < list.size(); i++) {
			compileNode((TreeNode) list.get(i), this.commandList);
		}
		this.commandList.add("RETURN:");
		for (String m : this.methodList)
			this.commandList.add(m);
	}

	public void compileNode(TreeNode<String, ZhtmlFragment> node, ArrayList<String> parentList) {
		ZhtmlFragment tf = (ZhtmlFragment) node.getValue();
		if (tf.Type == 1) {
			String[] arr = tf.FragmentText.split("\\n");
			for (int i = 0; i < arr.length; i++) {
				String line = arr[i];
				if (i != arr.length - 1) {
					if (line.endsWith("\r")) {
						line = line.substring(0, line.length() - 1);
					}
					parentList.add("PRINTLN:" + StringUtil.javaEncode(line));
				} else {
					parentList.add("PRINT:" + StringUtil.javaEncode(line));
				}
			}
		}
		if (tf.Type == 3) {
			parentList.add("EVALHOLDER:" + tf.FragmentText);
		}
		if (tf.Type == 4) {
			if (tf.FragmentText.trim().startsWith("@")) {
				return;
			}
			if (tf.FragmentText.startsWith("="))
				parentList.add("SCRIPT:print," + StringUtil.javaEncode(tf.FragmentText.substring(1)));
			else {
				parentList.add("SCRIPT:" + StringUtil.javaEncode(tf.FragmentText));
			}
		}
		if (tf.Type == 2)
			try {
				compileTag(node, parentList);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void compileTag(TreeNode<String, ZhtmlFragment> node, ArrayList<String> parentList) throws Exception {
		ZhtmlFragment tf = (ZhtmlFragment) node.getValue();
		ZhtmlTag tag = this.parser.getTag(tf.TagPrefix, tf.TagName);
		String methodName = tf.TagPrefix + "_" + tf.TagName + "_" + this.VarID++;

		parentList.add("INVOKETAG:" + methodName);

		ArrayList list = new ArrayList();
		list.add("DEFINETAG:" + methodName);

		list.add("INITTAG:new," + this.parser.getTag(tf.TagPrefix, tf.TagName).TagLibURI + "," + tf.TagName);
		list.add("INITTAG:setStartLineNo," + tf.StartLineNo);
		for (Iterator localIterator = tf.Attributes.keySet().iterator(); localIterator.hasNext();) {
			Object k = localIterator.next();
			list.add("INITTAG:setAttribute," + k + "," + (String) tf.Attributes.get(k));
		}

		list.add("ONENTER:");
		list.add("IF:ISSKIP");

		if (StringUtil.isNotEmpty(tf.FragmentText)) {
			list.add("BEFOREBODY:");
			if (tag.isIterative()) {
				list.add("FOR:");
				ArrayList nodeList = node.getChildren();
				for (int i = 0; i < nodeList.size(); i++) {
					compileNode((TreeNode) nodeList.get(i), list);
				}
				list.add("ENDFOR:");
			} else {
				ArrayList nodeList = node.getChildren();
				for (int i = 0; i < nodeList.size(); i++) {
					compileNode((TreeNode) nodeList.get(i), list);
				}
			}
			list.add("AFTERBODY:");
		}
		list.add("ENDIF:");

		list.add("IF:ISEND");
		list.add("RETURN:");
		list.add("ENDIF:");

		list.add("ONEXIT:");
		list.add("ENDTAG:");

		this.methodList.add(StringUtil.join(list, "\n"));
	}

	public ZhtmlExecutor getExecutor() {
		return this.executor;
	}
}