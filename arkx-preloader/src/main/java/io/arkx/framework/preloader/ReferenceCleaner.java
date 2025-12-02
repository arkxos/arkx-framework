package io.arkx.framework.preloader;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class ReferenceCleaner {

	private static final List<String> JVM_THREAD_GROUP_NAMES = new ArrayList();

	private static final String JVN_THREAD_GROUP_SYSTEM = "system";

	private PreClassLoader loader;

	static {
		JVM_THREAD_GROUP_NAMES.add("system");
		JVM_THREAD_GROUP_NAMES.add("RMI Runtime");
	}

	public ReferenceCleaner(PreClassLoader loader) {
		this.loader = loader;
	}

	protected void clearReferences() {
		clearReferencesJdbc();

		clearReferencesThreads();

		clearReferencesThreadLocals();

		clearReferencesRmiTargets();

		clearReferencesStaticFinal();
		try {
			Class<?> clazz = Class.forName("org.apache.commons.logging.LogFactory");
			if (clazz != null) {
				Method m = clazz.getMethod("release", new Class[] { ClassLoader.class });
				m.invoke(null, new Object[] { this.loader });
			}
		}
		catch (Exception localException) {
		}
		clearReferencesResourceBundles();

		Introspector.flushCaches();

		this.loader = null;
	}

	private final void clearReferencesJdbc() {
		InputStream is;
		byte classBytes[];
		int offset;
		is = loader.getResourceAsStream("com/arkxos/preloader/JdbcLeakPrevention.class");
		classBytes = new byte[2048];
		offset = 0;
		try {
			for (int read = is.read(classBytes, offset, classBytes.length - offset); read > -1; read = is
				.read(classBytes, offset, classBytes.length - offset)) {
				offset += read;
				if (offset == classBytes.length) {
					byte tmp[] = new byte[classBytes.length * 2];
					System.arraycopy(classBytes, 0, tmp, 0, classBytes.length);
					classBytes = tmp;
				}
			}

			Class lpClass = loader.defineClassEx("io.arkx.preloader.JdbcLeakPrevention", classBytes, 0, offset,
					getClass().getProtectionDomain());
			Object obj = lpClass.newInstance();
			List driverNames = (List) obj.getClass()
				.getMethod("clearJdbcDriverRegistrations", new Class[0])
				.invoke(obj, new Object[0]);
			String name;
			for (Iterator iterator = driverNames.iterator(); iterator.hasNext(); System.out
				.println((new StringBuilder("Clear JDBC reference success:")).append(name).toString()))
				name = (String) iterator.next();

		}
		catch (Exception e) {
			System.out.println((new StringBuilder("Clear JDBC reference failed:")).append(e.getMessage()).toString());
		}
		if (is != null)
			try {
				is.close();
			}
			catch (IOException ioexception) {
			}
	}

	private final void clearReferencesStaticFinal() {
		Class[] arr = new Class[this.loader.getLoadedClasses().size()];
		arr = (Class[]) this.loader.getLoadedClasses().toArray(arr);
		Class[] arrayOfClass1;
		int j = (arrayOfClass1 = arr).length;
		for (int k = 0; k < j; k++) {
			Class<?> clazz = arrayOfClass1[k];
			try {
				Field[] fields = clazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					if (Modifier.isStatic(fields[i].getModifiers())) {
						fields[i].get(null);
						break;
					}
				}
			}
			catch (Throwable localThrowable1) {
			}
		}
		j = (arrayOfClass1 = arr).length;
		for (Class<?> clazz : arrayOfClass1) {
			// Class<?> clazz = arrayOfClass1[i];
			try {
				Field[] fields = clazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					int mods = field.getModifiers();
					if ((!field.getType().isPrimitive()) && (field.getName().indexOf("$") == -1)) {
						if (Modifier.isStatic(mods)) {
							try {
								field.setAccessible(true);
								if (Modifier.isFinal(mods)) {
									if ((!field.getType().getName().startsWith("java."))
											&& (!field.getType().getName().startsWith("javax."))) {
										nullInstance(field.get(null));
									}
								}
								else {
									field.set(null, null);
								}
							}
							catch (Throwable localThrowable2) {
							}
						}
					}
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void nullInstance(Object instance) {
		if (instance == null) {
			return;
		}
		Field[] fields = instance.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			int mods = field.getModifiers();
			if ((!field.getType().isPrimitive()) && (field.getName().indexOf("$") == -1)) {
				try {
					field.setAccessible(true);
					if ((!Modifier.isStatic(mods)) || (!Modifier.isFinal(mods))) {
						Object value = field.get(instance);
						if (value != null) {
							Class<? extends Object> valueClass = value.getClass();
							if (loadedByThisOrChild(valueClass)) {
								field.set(instance, null);
							}
						}
					}
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	protected boolean loadedByThisOrChild(Class<?> clazz) {
		boolean result = false;
		for (ClassLoader classLoader = clazz.getClassLoader(); classLoader != null; classLoader = classLoader
			.getParent()) {
			if (classLoader.equals(this.loader)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean threadNeedClear(Thread thread) {
		if (thread == Thread.currentThread()) {
			return false;
		}
		ThreadGroup tg = thread.getThreadGroup();
		if ((tg != null) && (JVM_THREAD_GROUP_NAMES.contains(tg.getName()))) {
			if (thread.getName().equals("Keep-Alive-Timer")) {
				thread.setContextClassLoader(this.loader.getParent());
			}
			return false;
		}
		if (!thread.isAlive()) {
			return false;
		}
		if (thread.getContextClassLoader() == this.loader) {
			return true;
		}
		if (thread.getClass().getClassLoader() == this.loader) {
			return true;
		}
		Object target = null;
		String[] arrayOfString;
		int j = (arrayOfString = new String[] { "target", "runnable", "action" }).length;
		for (int i = 0; i < j; i++) {
			String fieldName = arrayOfString[i];
			try {
				Field targetField = thread.getClass().getDeclaredField(fieldName);
				targetField.setAccessible(true);
				target = targetField.get(thread);
			}
			catch (Exception localException) {
			}
		}
		if ((target != null) && (target.getClass().getClassLoader() == this.loader)) {
			return true;
		}
		return false;
	}

	private void clearReferencesThreads() {
		Thread[] threads = getThreads();
		Thread[] arrayOfThread1;
		int j = (arrayOfThread1 = threads).length;
		for (int i = 0; i < j; i++) {
			Thread thread = arrayOfThread1[i];
			if (thread != null) {
				boolean clear = threadNeedClear(thread);
				if (clear) {
					if (thread.getClass().getName().startsWith("java.util.Timer")) {
						clearReferencesStopTimerThread(thread);
					}
					else {
						try {
							Object target = null;
							String[] arrayOfString;
							int m = (arrayOfString = new String[] { "target", "runnable", "action" }).length;
							for (int k = 0; k < m; k++) {
								String fieldName = arrayOfString[k];
								try {
									Field targetField = thread.getClass().getDeclaredField(fieldName);
									targetField.setAccessible(true);
									target = targetField.get(thread);
								}
								catch (NoSuchFieldException localNoSuchFieldException) {
								}
							}
							if ((target != null) && (target.getClass().getCanonicalName() != null)
									&& (target.getClass()
										.getCanonicalName()
										.equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
								Field executorField = target.getClass().getDeclaredField("this$0");
								executorField.setAccessible(true);
								Object executor = executorField.get(target);
								if ((executor instanceof ThreadPoolExecutor)) {
									((ThreadPoolExecutor) executor).shutdownNow();
								}
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						try {
							thread.notifyAll();
							thread.interrupt();
						}
						catch (Throwable localThrowable) {
						}
						try {
							thread.stop();
						}
						catch (Throwable localThrowable1) {
						}
					}
				}
			}
		}
	}

	private void clearReferencesStopTimerThread(Thread thread) {
		try {
			try {
				Field newTasksMayBeScheduledField = thread.getClass().getDeclaredField("newTasksMayBeScheduled");
				newTasksMayBeScheduledField.setAccessible(true);
				Field queueField = thread.getClass().getDeclaredField("queue");
				queueField.setAccessible(true);

				Object queue = queueField.get(thread);

				Method clearMethod = queue.getClass().getDeclaredMethod("clear", new Class[0]);
				clearMethod.setAccessible(true);
				synchronized (queue) {
					newTasksMayBeScheduledField.setBoolean(thread, false);
					clearMethod.invoke(queue, new Object[0]);
					queue.notify();
				}
			}
			catch (NoSuchFieldException nfe) {
				Method cancelMethod = thread.getClass().getDeclaredMethod("cancel", new Class[0]);
				synchronized (thread) {
					cancelMethod.setAccessible(true);
					cancelMethod.invoke(thread, new Object[0]);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearReferencesThreadLocals() {
		Thread[] threads = getThreads();
		try {
			Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
			inheritableThreadLocalsField.setAccessible(true);

			Class<?> tlmClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
			Field tableField = tlmClass.getDeclaredField("table");
			tableField.setAccessible(true);
			Method expungeStaleEntriesMethod = tlmClass.getDeclaredMethod("expungeStaleEntries", new Class[0]);
			expungeStaleEntriesMethod.setAccessible(true);
			for (int i = 0; i < threads.length; i++) {
				if (threads[i] != null) {
					Object threadLocalMap = threadLocalsField.get(threads[i]);
					if (threadLocalMap != null) {
						expungeStaleEntriesMethod.invoke(threadLocalMap, new Object[0]);
						checkThreadLocalMapForLeaks(threadLocalMap, tableField);
					}
					threadLocalMap = inheritableThreadLocalsField.get(threads[i]);
					if (threadLocalMap != null) {
						expungeStaleEntriesMethod.invoke(threadLocalMap, new Object[0]);
						checkThreadLocalMapForLeaks(threadLocalMap, tableField);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkThreadLocalMapForLeaks(Object map, Field internalTableField)
			throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
		if (map != null) {
			Method mapRemove = map.getClass().getDeclaredMethod("remove", new Class[] { ThreadLocal.class });
			mapRemove.setAccessible(true);
			Object[] table = (Object[]) internalTableField.get(map);
			int staleEntriesCount = 0;
			if (table != null) {
				for (int j = 0; j < table.length; j++) {
					if (table[j] != null) {
						boolean remove = false;

						Object key = ((Reference) table[j]).get();
						if ((equals(key)) || (isLoadedByThisWebappClassLoader(key))) {
							remove = true;
						}
						Field valueField = table[j].getClass().getDeclaredField("value");
						valueField.setAccessible(true);
						Object value = valueField.get(table[j]);
						if ((equals(value)) || (isLoadedByThisWebappClassLoader(value))) {
							remove = true;
						}
						if (remove) {
							if (key == null) {
								staleEntriesCount++;
							}
							else {
								mapRemove.invoke(map, new Object[] { key });
							}
						}
					}
				}
			}
			if (staleEntriesCount > 0) {
				Method mapRemoveStale = map.getClass().getDeclaredMethod("expungeStaleEntries", new Class[0]);
				mapRemoveStale.setAccessible(true);
				mapRemoveStale.invoke(map, new Object[0]);
			}
		}
	}

	private boolean isLoadedByThisWebappClassLoader(Object o) {
		if (o == null) {
			return false;
		}
		for (ClassLoader loader = o.getClass().getClassLoader(); loader != null; loader = loader.getParent()) {
			if (loader == this.loader) {
				return true;
			}
		}
		return false;
	}

	private Thread[] getThreads() {
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		while (tg.getParent() != null) {
			tg = tg.getParent();
		}
		int threadCountGuess = tg.activeCount() + 50;
		Thread[] threads = new Thread[threadCountGuess];
		int threadCountActual = tg.enumerate(threads);
		while (threadCountActual == threadCountGuess) {
			threadCountGuess *= 2;
			threads = new Thread[threadCountGuess];

			threadCountActual = tg.enumerate(threads);
		}
		return threads;
	}

	private void clearReferencesRmiTargets() {
		try {
			Class<?> objectTargetClass = Class.forName("sun.rmi.transport.Target");
			Field cclField = objectTargetClass.getDeclaredField("ccl");
			cclField.setAccessible(true);

			Class<?> objectTableClass = Class.forName("sun.rmi.transport.ObjectTable");
			Field objTableField = objectTableClass.getDeclaredField("objTable");
			objTableField.setAccessible(true);
			Object objTable = objTableField.get(null);
			if (objTable == null) {
				return;
			}
			if ((objTable instanceof Map)) {
				Iterator<?> iter = ((Map) objTable).values().iterator();
				while (iter.hasNext()) {
					Object obj = iter.next();
					Object cclObject = cclField.get(obj);
					if (this == cclObject) {
						iter.remove();
					}
				}
			}
			Field implTableField = objectTableClass.getDeclaredField("implTable");
			implTableField.setAccessible(true);
			Object implTable = implTableField.get(null);
			if (implTable == null) {
				return;
			}
			if ((implTable instanceof Map)) {
				Iterator<?> iter = ((Map) implTable).values().iterator();
				while (iter.hasNext()) {
					Object obj = iter.next();
					Object cclObject = cclField.get(obj);
					if (this == cclObject) {
						iter.remove();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearReferencesResourceBundles() {
		try {
			Field cacheListField = ResourceBundle.class.getDeclaredField("cacheList");
			cacheListField.setAccessible(true);

			Map<?, ?> cacheList = (Map) cacheListField.get(null);

			Set<?> keys = cacheList.keySet();
			Field loaderRefField = null;

			Iterator<?> keysIter = keys.iterator();
			while (keysIter.hasNext()) {
				Object key = keysIter.next();
				if (loaderRefField == null) {
					loaderRefField = key.getClass().getDeclaredField("loaderRef");
					loaderRefField.setAccessible(true);
				}
				WeakReference<?> loaderRef = (WeakReference) loaderRefField.get(key);
				ClassLoader loader = (ClassLoader) loaderRef.get();
				while ((loader != null) && (loader != this.loader)) {
					loader = loader.getParent();
				}
				if (loader != null) {
					keysIter.remove();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
