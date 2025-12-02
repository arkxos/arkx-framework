package org.ark.framework.jaf;

/**
 * @class org.ark.framework.jaf.IExpressionContext
 *
 * @author Darkness
 * @date 2013-1-31 下午12:57:35
 * @version V1.0
 */
public interface IExpressionContext {
    Object eval(PlaceHolder paramPlaceHolder);
}
