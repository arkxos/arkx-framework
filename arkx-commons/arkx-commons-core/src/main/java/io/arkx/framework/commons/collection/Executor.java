package io.arkx.framework.commons.collection;

/**
 * @class org.ark.framework.collection.Executor 执行器抽象类
 *
 * @author Darkness
 * @date 2012-8-6 下午9:59:48
 * @version V1.0
 */
public abstract class Executor {

    protected Object param;

    public Executor(Object param) {
        this.param = param;
    }

    public abstract boolean execute();
}
