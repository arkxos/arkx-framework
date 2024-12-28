package com.rapidark.framework.cosyui.zhtml;

import java.util.concurrent.locks.ReentrantLock;

import com.rapidark.framework.cosyui.expression.DefaultFunctionMapper;
import com.rapidark.framework.cosyui.expression.IFunction;
import com.rapidark.framework.cosyui.expression.IFunctionMapper;
import com.rapidark.framework.extend.AbstractExtendService;

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
					instance = findInstance(ZhtmlFunctionService.class);
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
