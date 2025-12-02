package io.arkx.framework.cloud.sidecar;

import java.net.URI;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SidecarConfig {

	private String name;

	/**
	 * polyglot service's ip.
	 */
	private String ip;

	/**
	 * polyglot service's port.
	 */
	@NotNull
	@Max(65535)
	@Min(1)
	private Integer port;

	/**
	 * polyglot service's health check url. this endpoint must return json and the format
	 * must follow spring boot actuator's health endpoint. eg. {"status": "UP"}.
	 */
	private URI healthCheckUrl;

	/**
	 * interval of health check.
	 */
	private long healthCheckInterval = 30_000L;

}
