package org.ark.framework.core.castor;

/**
 * castor转换器抽象类，提供id，name为类名称
 *
 * @author Darkness
 * @date 2013-3-26 下午07:40:16
 * @version V1.0
 */
public abstract class AbstractInnerCastor implements ICastor {

    @Override
    public String getExtendItemID() {
        return getClass().getName();
    }

    @Override
    public String getExtendItemName() {
        return getClass().getName();
    }

}
