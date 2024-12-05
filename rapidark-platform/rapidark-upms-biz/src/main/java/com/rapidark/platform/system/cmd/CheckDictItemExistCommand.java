package com.rapidark.platform.system.cmd;

import lombok.Data;

@Data
public class CheckDictItemExistCommand {

	private String dictCode;
	private String dictItemLabel;

}
