package io.arkx.framework.cosyui.zhtml;

import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.cosyui.template.ITemplateSourceProcessor;
import io.arkx.framework.extend.AbstractExtendService;

/**
 * Zhtml源代码处理器扩展服务。
 *
 */
public class ZhtmlSourceProcessorService extends AbstractExtendService<ITemplateSourceProcessor> {

	private static ZhtmlSourceProcessorService instance;

	private static ReentrantLock lock = new ReentrantLock();

	public static ZhtmlSourceProcessorService getInstance() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = AbstractExtendService.findInstance(ZhtmlSourceProcessorService.class);
				}
			}
			finally {
				lock.unlock();
			}
		}
		return instance;
	}

}
