package com.arkxos.framework.extend.plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.data.xml.XMLElement;
import com.arkxos.framework.data.xml.XMLParser;
import com.arkxos.framework.extend.ExtendActionConfig;
import com.arkxos.framework.extend.ExtendItemConfig;
import com.arkxos.framework.extend.ExtendPointConfig;
import com.arkxos.framework.extend.ExtendServiceConfig;
import com.arkxos.framework.i18n.LangUtil;

/**
 * 插件配置信息，对应一个.plugin文件
 * @private
 * @author Darkness
 * @date 2012-8-5 下午7:52:13 
 * @version V1.0
 */
public class PluginConfig {
	// 扩展点列表
	private Mapx<String, ExtendPointConfig> extendPoints = new Mapx<>();
	// 扩展服务列表
	private Mapx<String, ExtendServiceConfig> extendServices = new Mapx<>();
	private Mapx<String, String> requiredExtendPoints = new Mapx<>();
	private Mapx<String, String> requiredExtendServices = new Mapx<>();
	// 依赖的插件
	private Mapx<String, String> requiredPlugins = new Mapx<>();
	// 扩展行为列表
	private Mapx<String, ExtendActionConfig> extendActions = new Mapx<>();
	// 扩展项列表
	private Mapx<String, ExtendItemConfig> extendItems = new Mapx<>();
	
	private String ID;//ID 
	private String Name;//名称
	private String ClassName;
	private String author;//作者
	private String provider;//供应商
	private String version;//版本
	private String description;//备注
	private String updateSite;
	private String packageFile;// 所属文件包：xxx.jar or folder
	private boolean UIAsJar;//zhtmlInJar
	private boolean resourceAsJar;
	private boolean enabled;//状态
	private boolean running;
	// 文件列表
	private Set<String> pluginFiles = new HashSet<>();
	private String defaultCrudJavaPath;
	private String defaultCrudUIPath;

	public boolean isResourceAsJar() {
		return resourceAsJar;
	}

	public void setResourceAsJar(boolean resourceAsJar) {
		this.resourceAsJar = resourceAsJar;
	}

	public Mapx<String, ExtendPointConfig> getExtendPoints() {
		return extendPoints;
	}

	public Mapx<String, ExtendServiceConfig> getExtendServices() {
		return extendServices;
	}

	public Mapx<String, String> getRequiredExtendPoints() {
		return requiredExtendPoints;
	}

	public Mapx<String, String> getRequiredPlugins() {
		return requiredPlugins;
	}

	public Mapx<String, ExtendActionConfig> getExtendActions() {
		return extendActions;
	}

	public Mapx<String, ExtendItemConfig> getExtendItems() {
		return extendItems;
	}

	public String getID() {
		return ID;
	}

	public String getClassName() {
		return ClassName;
	}

	public String getAuthor() {
		return author;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public String getDescription(String language) {
		if (description == null) {
			return null;
		}
		return LangUtil.get(description, language);
	}

	public String getName() {
		return Name;
	}

	public String getName(String language) {
		if (Name == null) {
			return null;
		}
		return LangUtil.get(Name, language);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabling) {
		this.enabled = enabling;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getProvider() {
		return provider;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	XMLElement root;
	
	public XMLElement getRoot() {
		return root;
	}
	
	public void parse(String xml) throws PluginException {
		XMLParser parser = new XMLParser(xml);
		parser.parse();

		enabled = true;// 默认开启，待分析依赖关系时判断是否可用。

		// 基本信息
		root = parser.getDocument().getRoot();
		
		ID = root.elementText("id");
		Name = root.elementText("name");
		ClassName = root.elementText("class");
		description = root.elementText("description");
		version = root.elementText("version");
		author = root.elementText("author");
		provider = root.elementText("provider");
		updateSite = root.elementText("updateSite");
		defaultCrudJavaPath = root.elementText("defaultCrudJavaPath");
		defaultCrudUIPath = root.elementText("defaultCrudUIPath");
		UIAsJar = Boolean.parseBoolean(root.elementText("UIAsJar"));
		resourceAsJar = Boolean.parseBoolean(root.elementText("resourceAsJar"));

		if (ObjectUtil.isEmpty(updateSite)) {
			updateSite = "http://www.ark.com/update/";
		}

		if (ObjectUtil.isEmpty(ID)) {
			throw new PluginException("id is empty!");
		}
		if (ObjectUtil.isEmpty(Name)) {
			throw new PluginException("name is empty!");
		}
		if (ObjectUtil.isEmpty(version)) {
			throw new PluginException("version is empty!");
		}

		// 文件列表
		List<XMLElement> nds = root.elements("files.*");
		for (XMLElement nd : nds) {
			if (nd.getQName().equalsIgnoreCase("directory")) {
				pluginFiles.add("[D]" + nd.getText());
			}
			if (nd.getQName().equalsIgnoreCase("file")) {
				pluginFiles.add(nd.getText());
			}
		}

		// 依赖关系
		nds = root.elements("required.plugin");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				requiredPlugins.put(nd.getText(), nd.getAttributes().get("version"));
			}
		}

		// 扩展点
		nds = root.elements("extendPoint");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendPointConfig ep = new ExtendPointConfig();
				ep.init(this, nd);
				extendPoints.put(ep.getID(), ep);
			}
		}

		// 扩展服务
		nds = root.elements("extendService");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendServiceConfig ep = new ExtendServiceConfig();
				ep.init(this, nd);
				extendServices.put(ep.getID(), ep);
			}
		}

		// 扩展项
		nds = root.elements("extendItem");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendItemConfig ei = new ExtendItemConfig();
				ei.init(this, nd);
				extendItems.put(ei.getID(), ei);
				requiredExtendServices.put(ei.getExtendServiceID(), "Y");
			}
		}

		// 扩展行为
		nds = root.elements("extendAction");
		if (ObjectUtil.notEmpty(nds)) {
			for (XMLElement nd : nds) {
				ExtendActionConfig eac = new ExtendActionConfig();
				eac.init(this, nd);
				extendActions.put(eac.getID(), eac);
				requiredExtendPoints.put(eac.getExtendPointID(), "Y");
			}
		}
	}
	
	public Object extract(IPluginConfigExtractor extractor) {
		return extractor.extract(this);
	}

	public String getUpdateSite() {
		return updateSite;
	}

	public void setUpdateSite(String updateSite) {
		this.updateSite = updateSite;
	}

	public boolean isUIAsJar() {
		return UIAsJar;
	}

	public void setUIAsJar(boolean UIAsJar) {
		this.UIAsJar = UIAsJar;
	}

	@Override
	public String toString() {
		String str = super.toString();
		str = str.substring(str.lastIndexOf('@'));
		str = "plugin:" + getID() + str;
		return str;
	}

	public Set<String> getPluginFiles() {
		return pluginFiles;
	}

	public void setPluginFiles(Set<String> pluginFiles) {
		this.pluginFiles = pluginFiles;
	}

	public String getDefaultCrudJavaPath() {
		return defaultCrudJavaPath;
	}

	public void setDefaultCrudJavaPath(String defaultCrudJavaPath) {
		this.defaultCrudJavaPath = defaultCrudJavaPath;
	}

	public String getDefaultCrudUIPath() {
		return defaultCrudUIPath;
	}

	public void setDefaultCrudUIPath(String defaultCrudUIPath) {
		this.defaultCrudUIPath = defaultCrudUIPath;
	}

	public String getPackageFile() {
		return packageFile;
	}

	public void setPackageFile(String packageFile) {
		this.packageFile = packageFile;
	}
}
