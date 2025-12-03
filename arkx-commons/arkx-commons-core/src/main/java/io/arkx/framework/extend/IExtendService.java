package io.arkx.framework.extend;

import java.util.List;

/**
 * 扩展服务接口
 *
 * @author Darkness
 * @date 2012-8-7 下午9:19:43
 * @version V1.0
 */
public interface IExtendService<T extends IExtendItem> {

    void register(IExtendItem item);

    T get(String id);

    T remove(String id);

    List<T> getAll();

    void destory();

}
