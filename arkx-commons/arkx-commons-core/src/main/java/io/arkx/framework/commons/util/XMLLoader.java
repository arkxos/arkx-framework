package io.arkx.framework.commons.util;

import io.arkx.framework.commons.collection.CaseIgnoreMapx;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.collection.tree.TreeNode;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.data.xml.XMLParser;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @class org.ark.framework.utility.XMLLoader
 * xml加载器
 * 
 * 使用XMLParser替代
 * @see XMLParser
 * @author Darkness
 * @date 2012-8-6 下午9:56:05 
 * @version V1.0
 */
@Deprecated
public class XMLLoader {
	
	private Treex<String, NodeData> tree = new Treex<>();

	/**
	 * 加载xml文件路径，如果是文件，直接加载，如果是文件夹，加载文件夹下的所有xml结尾的文件
	 * 
	 * @author Darkness
	 * @date 2012-8-9 上午10:32:55 
	 * @version V1.0
	 */
	public void load(String path) {
		File f = new File(path);
		load(f);
	}

	/**
	 * 加载xml文件，如果是文件，直接加载，如果是文件夹，加载文件夹下的所有xml结尾的文件
	 * 
	 * @author Darkness
	 * @date 2012-8-9 上午10:33:12 
	 * @version V1.0
	 */
	public void load(File f) {
		if (f.isFile()) {
			loadOneFile(f);
		} else {
			File[] fs = f.listFiles();
			if(fs != null) {
				for (int i = 0; i < fs.length; i++) {
					f = fs[i];
					if ((f.isFile()) && (f.getName().toLowerCase().endsWith(".xml")))
						loadOneFile(f);
				}
			}
		}
	}

	/**
	 * 加载xml文件流
	 * 
	 * @author Darkness
	 * @date 2012-8-9 下午2:11:22 
	 * @version V1.0
	 */
	public void load(InputStream is) {
		loadOneFile(is);
	}

	/**
	 * 加载xml文件
	 * 
	 * @author Darkness
	 * @date 2012-8-9 下午2:11:36 
	 * @version V1.0
	 */
	private void loadOneFile(File f) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			loadOneFile(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void clear() {
		this.tree = new Treex<String, NodeData>();
	}

	/**
	 * 加载xml文件流
	 * 
	 * @author Darkness
	 * @date 2012-8-9 下午2:13:13 
	 * @version V1.0
	 */
	private void loadOneFile(InputStream is) {
		SAXReader reader = new SAXReader(false);
		try {
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			reader.setEntityResolver(new EntityResolver() {
				ByteArrayInputStream bs = new ByteArrayInputStream("".getBytes());

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(this.bs);
				}
			});
			Document doc = reader.read(is);
			Element root = doc.getRootElement();
			convertElement(root, this.tree.getRoot());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将xml节点转换成TreeNode<String, NodeData>
	 * 
	 * @author Darkness
	 * @date 2012-8-9 下午2:14:07 
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	private void convertElement(Element ele, TreeNode<String, NodeData> parent) {
		String name = ele.getName().toLowerCase();
		NodeData data = new NodeData();
		data.TagName = name;
		data.Body = ele.getTextTrim();
		List<Attribute> list = ele.attributes();
		Mapx<String, String> map = new Mapx<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Attribute attr = list.get(i);
			map.put(attr.getName(), attr.getValue());
		}
		data.Attributes = map;
		TreeNode<String, NodeData> node = parent.addChildByValue(data);
		data.treeNode = node;
		List<Element> listElement = ele.elements();
		for (int i = 0; i < listElement.size(); i++) {
			Element child = listElement.get(i);
			convertElement(child, node);
		}
	}

	/**
	 * 根据path获取节点数据
	 * 
	 * @author Darkness
	 * @date 2012-8-9 下午2:15:57 
	 * @version V1.0
	 */
	public NodeData[] getNodeDataList(String path) {
		String[] arr = path.split("\\.");
		TreeNode<String, NodeData> current = this.tree.getRoot();
		ArrayList<TreeNode<String, NodeData>> list = new ArrayList<TreeNode<String, NodeData>>();
		list.add(current);
		for (int i = 0; i < arr.length; i++) {
			list = getChildren(list, arr[i]);
			if (list == null) {
				return null;
			}
		}
		if (list.size() == 0) {
			return null;
		}
		NodeData[] datas = new NodeData[list.size()];
		for (int i = 0; i < list.size(); i++) {
			TreeNode<String, NodeData> node = list.get(i);
			datas[i] = node.getValue();
		}
		return datas;
	}

	private static ArrayList<TreeNode<String, NodeData>> getChildren(ArrayList<TreeNode<String, NodeData>> parentList, String pathPart) {
		ArrayList<TreeNode<String, NodeData>> list = new ArrayList<TreeNode<String, NodeData>>();
		for (int i = 0; i < parentList.size(); i++) {
			TreeNode<String, NodeData> node = parentList.get(i);
			List<TreeNode<String, NodeData>> nodes = node.getChildren();
			for (int j = 0; j < nodes.size(); j++) {
				NodeData data = nodes.get(j).getValue();
				if ((pathPart.equals("*")) || (data.getTagName().equalsIgnoreCase(pathPart))) {
					list.add(nodes.get(j));
				}
			}
		}
		return list;
	}

	public String getNodeBody(String path) {
		return getNodeBody(path, null, null);
	}

	public String getNodeBody(String path, String attrName, String attrValue) {
		NodeData nd = getNodeData(path, attrName, attrValue);
		if (nd == null) {
			return null;
		}
		return nd.Body;
	}

	public NodeData getNodeData(String path) {
		return getNodeData(path, null, null);
	}

	public NodeData getNodeData(String path, String attrName, String attrValue) {
		NodeData[] datas = getNodeDataList(path);
		if (ObjectUtil.notEmpty(datas)) {
			if (attrName == null) {
				return datas[0];
			}
			for (int i = 0; i < datas.length; i++) {
				String v = (String) datas[i].Attributes.get(attrName);
				if (v == null) {
					if (attrValue == null) {
						return datas[i];
					}
				} else if (v.equals(attrValue)) {
					return datas[i];
				}
			}
		}

		return null;
	}

	public static class NodeData {
		private Mapx<String, String> Attributes = new CaseIgnoreMapx<>();
		private String TagName;
		private String Body;
		private TreeNode<String, NodeData> treeNode;

		public Mapx<String, String> getAttributes() {
			return this.Attributes;
		}

		public String getTagName() {
			return this.TagName;
		}

		public String getBody() {
			return this.Body;
		}

		public TreeNode<String, NodeData> getTreeNode() {
			return this.treeNode;
		}

		public NodeData[] getChildrenDataList() {
			NodeData[] arr = new NodeData[this.treeNode.getChildren().size()];
			for (int i = 0; i < this.treeNode.getChildren().size(); i++) {
				arr[i] = this.treeNode.getChildren().get(i).getValue();
			}
			return arr;
		}

		public NodeData[] getChildrenDataList(String path) {
			String[] arr = path.split("\\.");
			ArrayList<TreeNode<String, NodeData>> list = new ArrayList<>();
			list.add(this.treeNode);
			for (int i = 0; i < arr.length; i++) {
				list = XMLLoader.getChildren(list, arr[i]);
				if (list == null) {
					return null;
				}
			}
			if (list.size() == 0) {
				return null;
			}
			NodeData[] datas = new NodeData[list.size()];
			for (int i = 0; i < list.size(); i++) {
				TreeNode<String, NodeData> node = list.get(i);
				datas[i] = ((NodeData) node.getValue());
			}
			return datas;
		}

		public String getChildNodeBody(String path) {
			return getChildNodeBody(path, null, null);
		}

		public String getChildNodeBody(String path, String attrName, String attrValue) {
			NodeData nd = getChildNodeData(path, attrName, attrValue);
			if (nd == null) {
				return null;
			}
			return nd.Body;
		}

		public NodeData getChildNodeData(String path) {
			return getChildNodeData(path, null, null);
		}

		public NodeData getChildNodeData(String path, String attrName, String attrValue) {
			NodeData[] datas = getChildrenDataList(path);
			if (ObjectUtil.notEmpty(datas)) {
				if (attrName == null) {
					return datas[0];
				}
				for (int i = 0; i < datas.length; i++) {
					String v = datas[i].Attributes.get(attrName);
					if (v == null) {
						if (attrValue == null) {
							return datas[i];
						}
					} else if (v.equals(attrValue)) {
						return datas[i];
					}
				}
			}

			return null;
		}
	}
}