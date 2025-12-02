package io.arkx.framework.preloader;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EmptyTimer extends Timer {

	public void schedule(TimerTask task, Date firstTime, long period) {
	}

	public void schedule(TimerTask task, Date time) {
	}

	public void schedule(TimerTask task, long delay) {
	}

	public void schedule(TimerTask task, long delay, long period) {
	}

	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
	}

	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
	}

}
