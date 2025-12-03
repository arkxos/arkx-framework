package io.arkx.framework.extend.plugin;

/**
 * @class org.ark.framework.extend.plugin.IPlugin 插件接口
 * @private
 * @author Darkness
 * @date 2012-8-7 下午9:22:00
 * @version V1.0
 */
public interface IPlugin {

    /**
     * 插件安装
     */
    void install() throws PluginException;// NO_UCD

    /**
     * 插件卸载
     */
    void uninstall() throws PluginException;// NO_UCD

    /**
     * 插件启动
     */
    void start() throws PluginException;

    /**
     * 插件停用
     */
    void stop() throws PluginException;

    /**
     * 应用停止时调用本方法
     */
    void destory();

    /**
     * 获得插件配置
     */
    PluginConfig getConfig();

}
