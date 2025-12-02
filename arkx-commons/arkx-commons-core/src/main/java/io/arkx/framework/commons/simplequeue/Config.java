package io.arkx.framework.commons.simplequeue;

import java.util.UUID;

/**
 * Object contains setting for crawler.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @see com.queue.processor.PageProcessor
 * @since 0.1.0
 */
public class Config {

	private int sleepTime = 5000;

	private int retryTimes = 0;

	private int cycleRetryTimes = 10;

	private int retrySleepTime = 1000;

	/**
	 * new a Site
	 * @return new site
	 */
	public static Config me() {
		return new Config();
	}

	/**
	 * Set the interval between the processing of two pages.<br>
	 * Time unit is micro seconds.<br>
	 * @param sleepTime sleepTime
	 * @return this
	 */
	public Config setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
		return this;
	}

	/**
	 * Get the interval between the processing of two pages.<br>
	 * Time unit is micro seconds.<br>
	 * @return the interval between the processing of two pages,
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * Get retry times immediately when download fail, 0 by default.<br>
	 * @return retry times when download fail
	 */
	public int getRetryTimes() {
		return retryTimes;
	}

	/**
	 * Set retry times when download fail, 0 by default.<br>
	 * @param retryTimes retryTimes
	 * @return this
	 */
	public Config setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
		return this;
	}

	/**
	 * When cycleRetryTimes is more than 0, it will add back to scheduler and try download
	 * again. <br>
	 * @return retry times when download fail
	 */
	public int getCycleRetryTimes() {
		return cycleRetryTimes;
	}

	/**
	 * Set cycleRetryTimes times when download fail, 0 by default. <br>
	 * @param cycleRetryTimes cycleRetryTimes
	 * @return this
	 */
	public Config setCycleRetryTimes(int cycleRetryTimes) {
		this.cycleRetryTimes = cycleRetryTimes;
		return this;
	}

	public int getRetrySleepTime() {
		return retrySleepTime;
	}

	/**
	 * Set retry sleep times when download fail, 1000 by default. <br>
	 * @param retrySleepTime retrySleepTime
	 * @return this
	 */
	public Config setRetrySleepTime(int retrySleepTime) {
		this.retrySleepTime = retrySleepTime;
		return this;
	}

	public Task toTask() {
		return new Task() {
			@Override
			public String getUUID() {
				String uuid = null;// Config.this.getDomain();
				if (uuid == null) {
					uuid = UUID.randomUUID().toString();
				}
				return uuid;
			}

			@Override
			public Config getSite() {
				return Config.this;
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Config site = (Config) o;

		if (cycleRetryTimes != site.cycleRetryTimes)
			return false;
		if (retryTimes != site.retryTimes)
			return false;
		if (sleepTime != site.sleepTime)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 31 * result + sleepTime;
		result = 31 * result + retryTimes;
		result = 31 * result + cycleRetryTimes;
		return result;
	}

	@Override
	public String toString() {
		return "Site{" + ", sleepTime=" + sleepTime + ", retryTimes=" + retryTimes + ", cycleRetryTimes="
				+ cycleRetryTimes + '}';
	}

}
