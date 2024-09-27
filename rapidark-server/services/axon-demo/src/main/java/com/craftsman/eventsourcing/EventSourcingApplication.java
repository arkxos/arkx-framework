package com.craftsman.eventsourcing;

import com.craftsman.eventsourcing.stream.channel.InputChannel;
import com.craftsman.eventsourcing.stream.channel.OutputChannel;
import com.rapidark.boot.RapidArkApplication;
import org.springframework.boot.SpringApplication;
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
