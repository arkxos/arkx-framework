package com.arkxos.framework.extend;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import com.arkxos.framework.extend.exception.CreateExtendActionInstanceException;

/**
 * @class org.ark.framework.extend.ExtendManager
 * <h2>扩展点管理器</h2>
 * 
 * 在需要扩展点的地方调用，调用方式如下：
 * <pre>
 * ExtendManager.invoke(EntityDeleteExtendAction.ExtendPointID, new Object[]{tableName, ids});
 * </pre>
 * <h4>扩展点行为定义</h4>
<pre>
public abstract class EntityDeleteExtendAction  implements IExtendAction {
	
	public static String ExtendPointID = "org.ark.framework.EntityDelete";

	public Object execute(Object[] params) throws ExtendException {
		return afterEntityDelete((String)params[0], (String)params[1]);
	}
	
	// 实体删除后操作
	public abstract boolean afterEntityDelete(String tableName, String ids);

}

&lt;!-- 在plugin文件中注册扩展点 -->
&lt;plugin>
  &lt;extendPoint>
    &lt;id>org.ark.framework.EntityDelete&lt;/id>
    &lt;class>org.ark.framework.infrastructure.repositories.EntityDeleteExtendAction&lt;/class>
    &lt;description>ORM Entity Delete Action&lt;/description>
    &lt;UIFlag>false&lt;/UIFlag>
  &lt;/extendPoint>
&lt;/plugin>
</pre>
	
<h4>自定义扩展行为</h4>
<pre>
public class CascadeDeleteCheck extends EntityDeleteExtendAction {

	private MetaModelRepository metaModelRepository = new MetaModelRepository();
	
	public boolean afterEntityDelete(String tableName, String ids) {
		// 自己的扩展行为
		return true;
	}

}

&lt;!-- 在plugin文件中注册扩展行为 -->
&lt;extendAction>
    &lt;id>org.ark.platform.EntityCascadeDeleteExtend&lt;/id>
    &lt;class>org.ark.os.app.meta.application.CascadeDeleteCheck&lt;/class>
    &lt;description>ORM Entity 级联删除处理&lt;/description>
    &lt;extendPoint>org.ark.framework.EntityDelete&lt;/extendPoint>
&lt;/extendAction>
</pre>
 * 
 * @author Darkness
 * @date 2012-8-5 下午7:41:53 
 * @version V1.0
 */
public class ExtendManager {
	
	private static ExtendManager instance = new ExtendManager();
	
	public static ExtendManager getInstance() {
		return instance;
	}

	// 扩展点-->行为
	private static Mapx<String, List<IExtendAction>> extendPointMap = new Mapx<>();

	// 扩展服务
	private static Mapx<String, IExtendService<IExtendItem>> extendServiceClassMap = new Mapx<>();

	public static void registerExtendPoint(String extendPointId) {
		if (extendPointMap.get(extendPointId) == null) {
			extendPointMap.put(extendPointId, new ArrayList<>());
		}
	}

	/**
	 * 注册扩展行为
	 * 
	 * @author Darkness
	 * @date 2012-12-5 下午04:58:13
	 * @version V1.0
	 */
	public static void registerExtendAction(String extendPointId, IExtendAction extendAction) {
		extendPointMap.get(extendPointId).add(extendAction);
	}

	/**
	 * 根据扩展点查找扩展行为
	 * 
	 * @author Darkness
	 * @date 2012-12-9 下午05:15:44
	 * @version V1.0
	 */
	public static List<IExtendAction> findExtendActions(String extendPointId) {
		return extendPointMap.get(extendPointId);
	}

	/**
	 * 注册扩展服务
	 * 
	 * @author Darkness
	 * @date 2012-12-5 下午05:08:02
	 * @version V1.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerExtendService(String extendServiceClassName, IExtendService extendService) {
		extendServiceClassMap.put(extendServiceClassName, extendService);
	}

	/**
	 * 根据类名查找扩展服务
	 * 
	 * @author Darkness
	 * @date 2012-12-5 下午05:09:59
	 * @version V1.0
	 */
	@SuppressWarnings("rawtypes")
	public static IExtendService findExtendServiceByClass(String className) {
		return extendServiceClassMap.get(className);
	}

	/**
	 * 调用扩展点
	 */
	public static Object[] invoke(String extendPointID, Object... args) {
		return instance.invokePoint(extendPointID, args);
	}
	
	/**
	 * 执行扩展点
	 * 
	 * @author Darkness
	 * @date 2012-8-6 下午10:14:38 
	 * @version V1.0
	 */
	public Object[] invokePoint(String extendPointID, Object... args) {
		try {
			if (!Config.isPluginContext()) {
				return new Object[] {};
			}
//			ExtendPluginProvider.start();
			if (!extendPointMap.containsKey(extendPointID)) {
				LogUtil.warn("ExtendPoint is not found:" + extendPointID);
				return new Object[] {};
			}
			List<IExtendAction> actions = extendPointMap.get(extendPointID);
			if (actions == null) {
				return null;
			}
			List<Object> r = new ArrayList<>();
			for (int i = 0; i < actions.size(); i++) {
				try {
					IExtendAction ea = actions.get(i);
					if (!ea.isUsable()) {
						continue;
					}
					r.add(ea.execute(args));
				} catch (CreateExtendActionInstanceException e) {
					e.printStackTrace();// extend action实例创建失败后只是输出异常
					actions.remove(i);
					i--;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return r.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
