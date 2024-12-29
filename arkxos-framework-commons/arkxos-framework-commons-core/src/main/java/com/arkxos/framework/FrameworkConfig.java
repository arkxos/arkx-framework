package com.arkxos.framework;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
	basePackages = { 
			"com.arkxos.framework",
	})
public class FrameworkConfig {

}
