package com.arkxos.framework.message.tcp.codec;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午4:04:26
 * @version 1.0
 * @since 1.0
 */
public final class MarshallingCodecFactory {

	/**
	 * 创建Jboss Marshaller
	 * 
	 * @return
	 * @throws IOException
	 */
	protected static Marshaller buildMarshalling() throws IOException {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
		return marshaller;
	}

	/**
	 * 创建Jboss Unmarshaller
	 * 
	 * @return
	 * @throws IOException
	 */
	protected static Unmarshaller buildUnMarshalling() throws IOException {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
		return unmarshaller;
	}
}
