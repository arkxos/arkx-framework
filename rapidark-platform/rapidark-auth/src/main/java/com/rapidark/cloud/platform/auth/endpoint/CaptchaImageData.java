package com.rapidark.cloud.platform.auth.endpoint;

import lombok.Getter;

@Getter
public class CaptchaImageData {

	private String verKey;
	private String image;

	public CaptchaImageData(String verKey, String image) {
		this.verKey = verKey;
		this.image = image;
	}

}
