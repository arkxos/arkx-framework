package io.arkx.framework.bean;

import com.arkxos.framework.commons.collection.CaseIgnoreMapx;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.exception.ServiceException;
import com.arkxos.framework.commons.util.lang.ClassUtil;

/**   
 * 
 * @author Darkness
 * @date 2013-12-14 下午04:35:24 
 * @version V1.0   
 */
public class AbstractXmlToBean {
	
	boolean ingoreRoot;
	
	public AbstractXmlToBean() {
		this(false);
	}
	
	public AbstractXmlToBean(boolean ingoreRoot) {
		this.ingoreRoot = ingoreRoot;
	}
	
	protected Object buildBean(String beanName, Mapx<String, Object> properties) {
		String beanAlias = beanName;
		String beanClassName = XmlToBean.beanAliasMap.get(beanAlias);
		Object bean = null;
		try {
			Class<?> beanClass = Class.forName(beanClassName);
			
			bean = ClassUtil.mapToObject(beanClass, new CaseIgnoreMapx<String, Object>(properties));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServiceException("类 " + beanClassName + " 不存在...");
		}

		return bean;
	}
}
