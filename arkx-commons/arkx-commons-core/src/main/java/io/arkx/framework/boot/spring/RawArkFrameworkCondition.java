package io.arkx.framework.boot.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RawArkFrameworkCondition implements Condition {

	@Value("${ark.isRawArkFramework}")
	private boolean isRawArkFramework;

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return isRawArkFramework;
	}

}
