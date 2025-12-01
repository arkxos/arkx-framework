package io.arkx.framework.data.mybatis.pro.service.adaptor.validator.impl;


import io.arkx.framework.data.mybatis.pro.service.adaptor.validator.ElementNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 
 * @author w.dehai
 *
 */
public class ElementNotEmptyValidator implements ConstraintValidator<ElementNotEmpty, Object[]> {

    @Override
    public boolean isValid(Object[] array, ConstraintValidatorContext context) {
        // 不判断数据为空的情况，此情况分发给其他的校验器。
        if (null == array || 0 == array.length) {
            return true;
        }
        for (int i = 0; i < array.length; i++) {
            if (null == array[i]) {
                return false;
            }
        }
        return true;
    }
}