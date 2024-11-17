package com.rapidark.cloud.platform.auth.endpoint;

import cn.hutool.core.lang.Validator;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.rapidark.framework.common.model.ResultBody;
import io.springboot.captcha.ArithmeticCaptcha;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.cloud.platform.common.core.constant.SecurityConstants;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码相关的接口
 *
 * @author lengleng
 * @date 2022/6/27
 */
@Schema(description = "图形验证码")
@RestController
@RequiredArgsConstructor
public class ImageCodeEndpoint {

	private static final Integer DEFAULT_IMAGE_WIDTH = 100;

	private static final Integer DEFAULT_IMAGE_HEIGHT = 40;

	private final RedisTemplate redisTemplate;

	/**
	 * 创建图形验证码
	 * @see ImageCodeEndpoint#generateCaptchaImage
	 */
	@Deprecated
	@SneakyThrows
	@GetMapping("/code/image")
	public void image(String verKey, HttpServletResponse response) {
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);

		if (Validator.isMobile(verKey)) {
			return;
		}

		String result = captcha.text();
		redisTemplate.opsForValue()
				.set(CacheConstants.DEFAULT_CODE_KEY + verKey, result,
						SecurityConstants.CODE_TIME, TimeUnit.SECONDS);
		// 转换流信息写出
		captcha.out(response.getOutputStream());
	}

	/**
	 * 获取png验证码
	 */
	@Schema(description = "获取png验证码", title = "获取png验证码")
	@SneakyThrows
	@GetMapping("/captcha/image")
	public ResultBody<CaptchaImageData> generateCaptchaImage() {
//		SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);

		String verCode = captcha.text();
		String verKey = UUID.randomUUID() + "";

//		redisUtil.set(key, verCode, 1800);
		// 存入redis并设置过期时间为30分钟
		redisTemplate.opsForValue()
				.set(CacheConstants.DEFAULT_CODE_KEY + verKey, verCode,
						SecurityConstants.CODE_TIME, TimeUnit.SECONDS);

		return ResultBody.ok(new CaptchaImageData(verKey, captcha.toBase64()));
	}

}
