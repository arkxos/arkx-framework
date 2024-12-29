/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rapidark.framework.cloud.sidecar;

import java.util.Objects;

import lombok.Data;
import org.springframework.boot.actuate.health.Status;

/**
 * @author yuhuangbin
 */
@Data
public class SidecarInstanceInfo {

	private String ip;

	private Integer port;

	private Status status;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SidecarInstanceInfo that = (SidecarInstanceInfo) o;
		return Objects.equals(ip, that.ip) && Objects.equals(port, that.port)
				&& Objects.equals(status, that.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ip, port, status);
	}

}
