package com.arkxos.framework.commons.util;

import java.lang.reflect.Field;
import java.util.TimerTask;

/**
 * 运行时修改TimerTask的执行周期
 * 
 * public static void main(String[] args){  
        ReschedulableTimerTask task=new ReschedulableTimerTask() {  
              
            @Override  
            public void run() {  
                System.out.println("RunTime:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));               
            }  
        };  
        Timer timer=new Timer();  
        timer.schedule(task, 2000, 2000);//每两秒执行一次  
        try {  
            Thread.sleep(6000);  
        } catch (InterruptedException e) {  
        }  
        //主线程6秒后，更改任务周期  
        task.setPeriod(1000);//改为每秒执行一次  
    } 
    
 * @author Darkness
 * @date 2014-4-17 下午3:28:36
 * @version V1.0
 */
public abstract class ReschedulableTimerTask extends TimerTask {

	/**
	 * 缩短周期，执行频率就提高
	 */
	public void setPeriod(long period) {
		// TimerTask并未提供修改period的公共方法，但我们可以借助java反射修改其值。
		setDeclaredField(TimerTask.class, this, "period", period);
	}

	// 通过反射修改字段的值
	static boolean setDeclaredField(Class<?> clazz, Object obj, String name, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean isRunning = false;

	private Object syncObject = new Object();

	@Override
	public void run() {
		if (!isRunning) {
			synchronized (syncObject) {
				if (!isRunning) {
					isRunning = true;

					running();

					isRunning = false;
				}
			}
		}
	}
	
	protected abstract void running();
}
