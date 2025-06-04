package io.arkx.framework.commons.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @class org.ark.framework.utility.ObjectHelper
 * @author Darkness
 * @date 2012-4-10 下午10:57:30
 * @version V1.0
 */
public class ObjectHelper {

	/**
	 * 对象深度克隆
	 * 
	 * @param originObj
	 *            --被克隆数据对象
	 * @return--克隆后数据对象
	 */
	public final static Object objectClone(Object originObj) {
		PipedOutputStream op = new PipedOutputStream();
		PipedInputStream ip = new PipedInputStream();
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			op.connect(ip);
			oos = new ObjectOutputStream(op);
			oos.writeObject(originObj);
			oos.close();
			ois = new ObjectInputStream(ip);
			Object obj = ois.readObject();
			ois.close();
			oos = null;
			ois = null;
			return obj;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				op.close();
				ip.close();
				op = null;
				ip = null;
				if (oos != null) {
					oos.close();
					oos = null;
				}
				if (ois != null) {
					ois.close();
					ois = null;
				}
			} catch (IOException e) {
			}
		}
	}

}