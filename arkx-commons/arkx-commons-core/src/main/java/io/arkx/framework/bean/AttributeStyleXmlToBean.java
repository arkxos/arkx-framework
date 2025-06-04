package io.arkx.framework.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.lang.ClassUtil;
import com.arkxos.framework.data.xml.XMLElement;
import com.arkxos.framework.data.xml.XMLMultiLoader;

/**
 * 根据xml的attribute转换成bean
 * @author Darkness
 * @date 2013-12-14 下午04:01:25
 * @version V1.0
 */
public class AttributeStyleXmlToBean extends AbstractXmlToBean {

	public AttributeStyleXmlToBean(boolean ingoreRoot) {
		super(ingoreRoot);
	}
	
	public List<Object> toBean(XMLMultiLoader xmlLoader) {
		XMLElement xmlElement = xmlLoader.getRoot();

		List<XMLElement> elements = xmlElement.getElements();
		List<Object> beans = dealChildren(elements.get(0), new Mapx<String, Object>());

		return beans;
	}

	public static String generatePk() {
        return UUID.randomUUID().toString();
    }
	
	private List<Object> dealChildren(XMLElement element, Mapx<String, Object> parentParams) {
		List<Object> result = new ArrayList<Object>();

		Mapx<String, Object> paramToChildren = null;

		String CHILDREN_MARK = "children-";
		Object bean = null;
		if (element.getQName().startsWith(CHILDREN_MARK)) {
			paramToChildren = parentParams;
		} else {// 非子节点，转换成bean
			parentParams.put("id", generatePk());
			parentParams.putAll(element.getAttributes());
			bean = buildBean(element.getQName(), parentParams);

			paramToChildren = new Mapx<String, Object>();
			Mapx<String, Object> parentProperties = ClassUtil.getPropertyValues(bean);

			paramToChildren.putAll(parentProperties, "parent.");

			result.add(bean);
		}

		for (XMLElement childElement : element.getElements()) {
			List<Object> childBeans = dealChildren(childElement, paramToChildren);
			result.addAll(childBeans);
		}

		return result;
	}

	
}
