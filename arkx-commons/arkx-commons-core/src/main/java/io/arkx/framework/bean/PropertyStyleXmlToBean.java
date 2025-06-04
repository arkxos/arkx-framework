package io.arkx.framework.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.lang.ReflectionUtil;
import com.arkxos.framework.data.xml.XMLElement;
import com.arkxos.framework.data.xml.XMLMultiLoader;

/**
 * 
 * @author Darkness
 * @date 2013-12-14 下午04:06:08
 * @version V1.0
 */
public class PropertyStyleXmlToBean extends AbstractXmlToBean {

	public PropertyStyleXmlToBean(boolean ingoreRoot) {
		super(ingoreRoot);
	}
	
	public List<Object> toBean(List<XMLElement> elements) {
		List<Object> result = new ArrayList<Object>();
		
		for (XMLElement element : elements) {
			List<Object> beans = dealChildren(element, new Mapx<String, Object>());
			result.addAll(beans);
		}
		
		Collections.reverse(result);
		return result;
	}
	
	public List<Object> toBean(XMLMultiLoader xmlLoader) {
		List<Object> result = new ArrayList<Object>();
		
		XMLElement xmlElement = xmlLoader.getRoot();

		List<XMLElement> elements = xmlElement.getElements();
		for (XMLElement rootElement : elements) {
			if(ingoreRoot) {
				List<XMLElement> realRootElements = rootElement.getElements();
				for (XMLElement realRootElement : realRootElements) {
					List<Object> beans = dealChildren(realRootElement, new Mapx<String, Object>());
					result.addAll(beans);
				}
			} else {
				List<Object> beans = dealChildren(rootElement, new Mapx<String, Object>());
				result.addAll(beans);
			}
		}
		Collections.reverse(result);
		return result;
	}

	String CHILDREN_MARK = "children-";
	
	public static String generatePk() {
        return UUID.randomUUID().toString();
    }
	
	private List<Object> dealChildren(XMLElement element, Mapx<String, Object> parentParams) {
		List<Object> result = new ArrayList<Object>();

		List<XMLElement> propertyElements = element.getElements();
		
		Mapx<String, Object> properties = new Mapx<String, Object>();
		
		Map<String, List<Object>> childs = new HashMap<String, List<Object>>();
		
		for (XMLElement propertyElement : propertyElements) {
			// 不是children开头的，都认为是property
			if (!propertyElement.getQName().startsWith(CHILDREN_MARK)) {
				properties.put(propertyElement.getQName(), propertyElement.getText());
				properties.put("id", generatePk());
				properties.putAll(parentParams, "parent.");
			} else {
				for (XMLElement childElement : propertyElement.getElements()) {
					List<Object> childBeans = dealChildren(childElement, properties);
					childs.put(propertyElement.getQName(), childBeans);
					
					result.addAll(childBeans);
				}
			}
		}

		Object bean = buildBean(element.getQName(), properties);
		result.add(bean);
		
		if(childs.size() > 0) {
			for (String key : childs.keySet()) {
				List<Object> childList = childs.get(key);
				if(childList.size() > 0) {
					String addChildMethodName = "add" + childList.get(0).getClass().getSimpleName();
					Method addChildMethod = ReflectionUtil.getDeclaredMethod(bean, addChildMethodName, childList.get(0).getClass());
					if(addChildMethod != null) {
						for (Object childBean : childList) {
							ReflectionUtil.invokeMethod(bean, addChildMethodName, new Class[]{childList.get(0).getClass()}, new Object[]{childBean});
						}
					}
				}
			}
			
		}
		
		return result;
	}

}
