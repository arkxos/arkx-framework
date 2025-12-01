package org.ark.framework.messages;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @class org.ark.framework.messages.MessageBus
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:30:44 
 * @version V1.0
 */
public class MessageBus {
	private static Mapx<String, ArrayList<String>> Types = new Mapx<String, ArrayList<String>>();

	static {
		loadConfig();
	}

	private static void loadConfig() {
		String path = Config.getContextRealPath() + "WEB-INF/classes/framework.xml";
		SAXReader reader = new SAXReader(false);
		try {
			Document doc = reader.read(new File(path));
			Element root = doc.getRootElement();
			Element messages = root.element("messages");
			if (messages != null) {
				List eles = messages.elements();
				for (int i = 0; i < eles.size(); i++) {
					Element message = (Element) eles.get(i);
					String name = message.attributeValue("name");
					List receivers = message.elements();
					ArrayList arr = new ArrayList();
					for (int j = 0; j < receivers.size(); j++) {
						Element receiver = (Element) receivers.get(i);
						arr.add(receiver.attributeValue("class"));
					}
					Types.put(name, arr);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static String[] getMessageNames(MessageReceiver receiver) {
		return null;
	}

	public static void send(MessageSender sender) {
		ArrayList list = (ArrayList) Types.get(sender.getMessageName());
		if (list == null) {
			throw new RuntimeException("Message type is not registered:" + sender.getMessageName());
		}
		for (int i = 0; i < list.size(); i++) {
			String className = (String) list.get(i);
			MessageReceiver r = null;
			try {
				r = (MessageReceiver) Class.forName(className).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Mapx feedback = r.receive(sender.getMessage());
			sender.receiveFeedback(feedback);
		}
	}
}