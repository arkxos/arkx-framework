package io.arkx.framework.data.fasttable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;  

/**
 *  
 * @author Darkness
 * @date 2016年11月9日 上午10:55:06
 * @version V1.0
 */
@Target({ ElementType.FIELD, ElementType.TYPE })  
@Retention(RetentionPolicy.CLASS)  
public @interface Serialize {  

}  