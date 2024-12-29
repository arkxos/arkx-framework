package com.arkxos.framework;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
	basePackages = { 
			"com.rapidark.framework",
	})
public class FrameworkConfig {

}
