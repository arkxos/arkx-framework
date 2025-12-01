package io.arkx.framework.commons.download;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("restriction")
public class AtomicIntegerAdapter extends XmlAdapter<Integer, AtomicInteger> {

	@Override
	public Integer marshal(AtomicInteger v) throws Exception {
		return v.get();
	}

	@Override
	public AtomicInteger unmarshal(Integer v) throws Exception {
		return new AtomicInteger(v);
	}

}
