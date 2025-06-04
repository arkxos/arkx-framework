package io.arkx.framework.data.jdbc;
/**   
 * @class org.ark.framework.infrastructure.domainbase.CodeEntity
 * 带编码的实体类
 * @author Darkness
 * @date 2013-3-17 下午07:59:02 
 * @version V1.0   
 */
public class CodeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	//获取编码
	private String code;
	
	/**
	 * 获取编码
	 * 
	 * @author Darkness
	 * @date 2013-3-17 下午08:01:39 
	 * @version V1.0
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置编码
	 * 
	 * @author Darkness
	 * @date 2013-3-17 下午08:01:43 
	 * @version V1.0
	 */
	public void setCode(String code) {
		this.code = code;
	}
}
