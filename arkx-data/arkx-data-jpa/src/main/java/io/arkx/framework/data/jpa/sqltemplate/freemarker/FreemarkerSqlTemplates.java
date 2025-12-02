package io.arkx.framework.data.jpa.sqltemplate.freemarker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import io.arkx.framework.data.jpa.sqltemplate.NamedTemplateResolver;
import io.arkx.framework.data.jpa.sqltemplate.SftlNamedTemplateResolver;
import io.arkx.framework.data.jpa.sqltemplate.XmlNamedTemplateResolver;
import io.arkx.framework.data.jpa.util.JpaConstants;

import cn.hutool.core.collection.ConcurrentHashSet;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;

/**
 * <p>
 * FreemarkerSql模板
 * </p>
 *
 * @author Darkness
 * @date 2020年10月29日 下午4:13:10
 * @version V1.0
 */
@Component
public class FreemarkerSqlTemplates implements ResourceLoaderAware, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	private static StringTemplateLoader sqlTemplateLoader = new StringTemplateLoader();

	static {
		cfg.setTemplateLoader(sqlTemplateLoader);
	}

	private String encoding = JpaConstants.ENCODING;

	// @PersistenceContext
	// private EntityManager em;

	private Map<String, Long> lastModifiedCache = new ConcurrentHashMap<>();

	private Map<String, List<Resource>> sqlResources = new ConcurrentHashMap<>();

	private String templateLocation = "classpath:/sqls";

	private String templateBasePackage = "**";

	private ResourceLoader resourceLoader;

	private String suffix = ".xml";

	private Boolean autoCheck = Boolean.TRUE; // 默认开启自动检测SQL文件的更新

	private Map<String, NamedTemplateResolver> suffixResolvers = new HashMap<>();

	private Set<EntityManager> entityManagers = new ConcurrentHashSet<>();

	private Lock lock = new ReentrantLock();

	{
		suffixResolvers.put(".sftl", new SftlNamedTemplateResolver());
	}

	// public FreemarkerSqlTemplates(EntityManager em) {
	// this.em = em;
	// }

	public void setEm(EntityManager em) {
		if (entityManagers.contains(em)) {
			return;
		}

		// 获取锁
		lock.lock();

		try {
			// 访问此锁保护的资源
			if (entityManagers.contains(em)) {
				return;
			}
			try {
				this.loadResources(em);
				entityManagers.add(em);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		finally {
			// 释放锁
			lock.unlock();
		}
	}

	public String process(String entityName, String methodName, Map<String, Object> model) {
		try {
			if (this.autoCheck && isModified(entityName)) {
				reloadTemplateResource(entityName);
			}

			Template template = this.getTemplate(entityName, methodName);
			assert template != null;

			StringWriter writer = new StringWriter();
			template.process(model, writer);
			String sql = writer.toString();
			logger.debug(sql);
			return sql;
		}
		catch (Exception e) {
			logger.error("process template error. Entity name: " + entityName + " methodName:" + methodName, e);
			return StringUtils.EMPTY;
		}
	}

	private Template getTemplate(String entityName, String methodName) {
		String templateKey = getTemplateKey(entityName, methodName);
		try {
			return cfg.getTemplate(templateKey, encoding);
		}
		catch (IOException e) {
			logger.error("Template not found for name {}", templateKey);
			return null;
		}
	}

	private String getTemplateKey(String entityName, String methodName) {
		return entityName + ":" + methodName;
	}

	private boolean isModified(final String entityName) {
		try {
			Long lastModified = lastModifiedCache.get(entityName);
			List<Resource> resourceList = sqlResources.get(entityName);
			// if (resourceList == null || resourceList.isEmpty()) {
			// this.loadResources();
			// }

			lastModified = lastModifiedCache.get(entityName);
			resourceList = sqlResources.get(entityName);
			if (resourceList == null || resourceList.isEmpty()) {
				return false;
			}

			long newLastModified = 0;
			for (Resource resource : resourceList) {
				if (newLastModified == 0) {
					newLastModified = resource.lastModified();
				}
				else {
					// get the last modified.
					newLastModified = newLastModified > resource.lastModified() ? newLastModified
							: resource.lastModified();
				}
			}

			// check modified for cache.
			if (lastModified == null || newLastModified > lastModified) {
				lastModifiedCache.put(entityName, newLastModified);
				return true;
			}
		}
		catch (Exception e) {
			logger.error("{}", e);
		}
		return false;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		XmlNamedTemplateResolver xmlNamedTemplateResolver = new XmlNamedTemplateResolver(resourceLoader);
		xmlNamedTemplateResolver.setEncoding(encoding);
		this.suffixResolvers.put(".xml", xmlNamedTemplateResolver);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// this.loadResources();
	}

	private void loadResources(EntityManager em) throws Exception {
		Set<String> entityNames = loadEntityNames(em);

		resolveSqlResource(entityNames);

		for (String entityName : entityNames) {
			if (isModified(entityName)) {
				reloadTemplateResource(entityName);
			}
		}
	}

	private void reloadTemplateResource(String entityName) throws Exception {
		logger.debug("load resource " + entityName);
		List<Resource> resourceList = sqlResources.get(entityName);
		if (resourceList == null) {
			return;
		}
		// process template.
		for (Resource resource : resourceList) {
			NamedTemplateResolver namedTemplateResolver = suffixResolvers.get(suffix);
			Iterator<Void> iterator = namedTemplateResolver.doInTemplateResource(resource, (templateName, content) -> {
				String key = getTemplateKey(entityName, templateName);
				Object src = sqlTemplateLoader.findTemplateSource(key);
				if (src != null) {
					logger.warn("found duplicate template key, will replace the value, key:" + key);
				}
				sqlTemplateLoader.putTemplate(getTemplateKey(entityName, templateName), content);
			});
			while (iterator.hasNext()) {
				iterator.next();
			}
		}
	}

	private void resolveSqlResource(Set<String> names) throws IOException {
		if (names.isEmpty()) {
			return;
		}

		String suffixPattern = "/**/*" + suffix;
		String pattern;
		if (StringUtils.isNotBlank(templateBasePackage)) {
			pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(templateBasePackage) + suffixPattern;

			loadPatternResource(names, pattern);
		}
		if (StringUtils.isNotBlank(templateLocation)) {
			pattern = templateLocation.contains(suffix) ? templateLocation : templateLocation + suffixPattern;
			try {
				loadPatternResource(names, pattern);
			}
			catch (FileNotFoundException e) {
				if ("classpath:/sqls".equals(templateLocation)) {
					// warn: default value
					logger.warn("templateLocation[" + templateLocation + "] not exist!");
					logger.warn(e.getMessage());
				}
				else {
					// throw: custom value.
					throw e;
				}
			}
		}
	}

	private Set<String> loadEntityNames(EntityManager em) {
		Set<String> names = new HashSet<>();
		if (em == null) {
			return names;
		}
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		for (EntityType<?> entity : entities) {
			names.add(entity.getName());
		}
		return names;
	}

	private void loadPatternResource(Set<String> names, String pattern) throws IOException {
		PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(
				resourceLoader);
		Resource[] resources = resourcePatternResolver.getResources(pattern);
		for (Resource resource : resources) {
			String resourceName = resource.getFilename().replace(suffix, "");
			if (names.contains(resourceName)) {
				// allow multi resource.
				List<Resource> resourceList;
				if (sqlResources.containsKey(resourceName)) {
					resourceList = sqlResources.get(resourceName);
				}
				else {
					resourceList = new LinkedList<>();
					sqlResources.put(resourceName, resourceList);
				}
				resourceList.add(resource);
			}
		}
	}

	public void setTemplateLocation(String templateLocation) {
		this.templateLocation = templateLocation;
	}

	public void setTemplateBasePackage(String templateBasePackage) {
		this.templateBasePackage = templateBasePackage;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setAutoCheck(Boolean autoCheck) {
		this.autoCheck = autoCheck;
	}

}
