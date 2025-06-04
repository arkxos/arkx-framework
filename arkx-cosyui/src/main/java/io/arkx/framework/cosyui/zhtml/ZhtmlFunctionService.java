package io.arkx.framework.cosyui.zhtml;

import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.cosyui.expression.DefaultFunctionMapper;
import io.arkx.framework.cosyui.expression.IFunction;
import io.arkx.framework.cosyui.expression.IFunctionMapper;
import io.arkx.framework.extend.AbstractExtendService;

/**
 * Zhtml模板函数扩展服务
 * 
 */
public class ZhtmlFunctionService extends AbstractExtendService<IFunction> {
	private static ZhtmlFunctionService instance;
	private static IFunctionMapper mapper;
	private static ReentrantLock lock = new ReentrantLock();

	public static ZhtmlFunctionService getInstance() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = AbstractExtendService.findInstance(ZhtmlFunctionService.class);
				}
			} finally {
				lock.unlock();
			}
		}
		return instance;
	}

	public static IFunctionMapper getFunctionMappper() {
		if (mapper == null) {
			lock.lock();
			try {
				if (mapper == null) {
					mapper = DefaultFunctionMapper.getInstance();
					for (IFunction f : getInstance().getAll()) {
						mapper.registerFunction(f);
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return mapper;
	}
}
