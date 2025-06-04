package org.ark.framework.jaf.zhtml;

import java.io.File;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.XMLLoader;


/**
 * @class org.ark.framework.jaf.zhtml.TagLibLoader
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:53:55 
 * @version V1.0
 */
public class TagLibLoader {
	
	private static Mapx<String, TagLib> libs;

	public static Mapx<String, TagLib> load() {
		if (libs == null) {
			libs = new Mapx<String, TagLib>();
			String path = Config.getClassesPath();
			if (path.indexOf("WEB-INF") > 0) {
				path = path.substring(0, path.lastIndexOf("WEB-INF") + 7);
			}
			File f = new File(path);
			for (File f2 : f.listFiles()) {
				if (f2.getName().toLowerCase().endsWith(".tld")) {
					TagLib lib = loadOneFile(f2);
					libs.put(lib.URI, lib);
				}
			}
		}
		return libs;
	}

	private static TagLib loadOneFile(File f) {
		XMLLoader loader = new XMLLoader();
		loader.load(f);
		TagLib lib = new TagLib();
		lib.JspVersion = loader.getNodeData("taglib.jspversion").getBody();
		lib.TagLibVersion = loader.getNodeData("taglib.tlibversion").getBody();
		lib.URI = loader.getNodeData("taglib.uri").getBody();
		lib.Tags = new Mapx<String, ZhtmlTag>();

		XMLLoader.NodeData[] list = loader.getNodeDataList("taglib.tag");
		for (XMLLoader.NodeData data : list) {
			ZhtmlTag tag = new ZhtmlTag();
			tag.TagClass = data.getChildNodeBody("tagclass");
			tag.TagName = data.getChildNodeBody("name");
			tag.TagLibURI = lib.URI;
			tag.Attributes = new Mapx();

			for (XMLLoader.NodeData child : data.getChildrenDataList("attribute")) {
				ZhtmlTag.JspTagAttribute attr = new ZhtmlTag.JspTagAttribute();
				attr.Name = child.getChildNodeBody("name");
				attr.Required = "true".equals(child.getChildNodeBody("required"));
				tag.Attributes.put(attr.Name, attr);
			}
			lib.Tags.put(tag.TagName, tag);
		}
		return lib;
	}

	public static ZhtmlTag getTag(String uri, String tagName) {
		TagLib lib = (TagLib) libs.get(uri);
		if (lib == null) {
			return null;
		}
		return (ZhtmlTag) lib.Tags.get(tagName);
	}

	public static class TagLib {
		public String URI;
		public String TagLibVersion;
		public String JspVersion;
		public Mapx<String, ZhtmlTag> Tags;
	}
}