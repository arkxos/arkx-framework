package com.rapidark.preloader;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;

public class ClassModifyScanner {
	ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
	private Timer mTimer;
	private TimerTask task;

	public void addClass(String path, long lastTime) {
		this.map.put(path, Long.valueOf(lastTime));
	}

	public void start() {
		File lib = new File(Util.getPluginPath() + "lib/");
		if (lib.exists()) {
			File[] arrayOfFile;
			int j = (arrayOfFile = lib.listFiles()).length;
			for (int i = 0; i < j; i++) {
				File f = arrayOfFile[i];
				this.map.put(f.getAbsolutePath(), f.lastModified());
			}
		}
		this.mTimer = new Timer("Class Modify Scanner", true);
		this.task = new TimerTask() {
			public void run() {
				ClassModifyScanner.this.check();
			}
		};
		this.mTimer.schedule(this.task, 5000L, 5000L);
	}

	public void check() {
		if (Reloader.isReloading) {
			return;
		}
		boolean changed = false;
		for (String k : this.map.keySet()) {
			File f = new File(k);
			if (!f.exists()) {
				changed = true;
				break;
			}
			if (f.lastModified() != ((Long) this.map.get(k)).longValue()) {
				changed = true;
				break;
			}
		}
		File lib = new File(Util.getPluginPath() + "lib/");
		if (lib.exists()) {
			File[] arrayOfFile;
			// File localFile1 = (arrayOfFile = ).length;
			for (File f : lib.listFiles()) {
				String k = f.getAbsolutePath();
				if (!this.map.containsKey(k)) {
					this.map.put(k, Long.valueOf(f.lastModified()));
					break;
				}
			}
		}
		if (changed) {
			try {
				if (!Reloader.isReloading) {
					Reloader.reload();
				}
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
	}

	public void destory() {
		if (this.mTimer != null) {
			this.mTimer.cancel();
		}
		if (this.task != null) {
			this.task.cancel();
		}
		this.map.clear();
		this.map = null;
		this.task = null;
		this.mTimer = null;
	}
}
