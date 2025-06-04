package io.arkx.framework.scheduling.quartz;

import org.quartz.JobKey;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.util.Assert;

/**
 * This is a workaround for @Autowired annotations in cron job classes.
 * @author Nobody
 * @date 2025-05-16 17:30
 * @since 1.0
 */
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

	private transient AutowireCapableBeanFactory beanFactory;

//	AutowiringSpringBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
//		Assert.notNull(beanFactory, "Bean factory must not be null");
//		this.beanFactory = beanFactory;
//	}

	@Override
	public void setApplicationContext(final ApplicationContext context) {
		beanFactory = context.getAutowireCapableBeanFactory();
	}

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		Object jobInstance = super.createJobInstance(bundle);
		this.beanFactory.autowireBean(jobInstance);

		// 此处必须注入 beanName 不然sentinel 报错
		JobKey jobKey = bundle.getTrigger().getJobKey();
		String beanName = jobKey + jobKey.getName();
		this.beanFactory.initializeBean(jobInstance, beanName);
		return jobInstance;
	}

}
