package io.arkx.framework.data.db.product.postgresql.copy.pgsql.converter;

import java.time.LocalDateTime;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.utils.TimeStampUtils;

public class LocalDateTimeConverter implements IValueConverter<LocalDateTime, Long> {

    @Override
    public Long convert(final LocalDateTime dateTime) {
        return TimeStampUtils.convertToPostgresTimeStamp(dateTime);
    }

}
