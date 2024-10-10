package com.xdreamaker.framework.ddd;

import com.xdreamaker.framework.ddd.stream.channel.InputChannel;
import com.xdreamaker.framework.ddd.stream.channel.OutputChannel;
import com.rapidark.boot.RapidArkApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableBinding( {InputChannel.class, OutputChannel.class})
@EnableAsync
public class EventSourcingApplication {

	public static void main(String[] args) {
		RapidArkApplication.run(EventSourcingApplication.class, args);
	}

}
