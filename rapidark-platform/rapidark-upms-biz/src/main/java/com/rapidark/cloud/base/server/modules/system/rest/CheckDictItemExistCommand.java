package com.rapidark.cloud.base.server.modules.system.rest;

import lombok.Data;

@Data
public class CheckDictItemExistCommand {

	private String dictCode;
	private String dictItemLabel;

}
