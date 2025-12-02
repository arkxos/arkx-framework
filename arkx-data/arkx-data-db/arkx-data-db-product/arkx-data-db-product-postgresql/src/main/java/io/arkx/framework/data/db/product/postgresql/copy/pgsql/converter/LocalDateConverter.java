package io.arkx.framework.data.db.product.postgresql.copy.pgsql.converter;

import java.time.LocalDate;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.utils.TimeStampUtils;

public class LocalDateConverter implements IValueConverter<LocalDate, Integer> {

    @Override
    public Integer convert(final LocalDate date) {
        return TimeStampUtils.toPgDays(date);
    }

}
