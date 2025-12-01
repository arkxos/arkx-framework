package io.arkx.framework.data.db.product.postgresql.copy.pgsql.converter;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.utils.TimeStampUtils;

import java.time.LocalDate;

public class LocalDateConverter implements IValueConverter<LocalDate, Integer> {

  @Override
  public Integer convert(final LocalDate date) {
    return TimeStampUtils.toPgDays(date);
  }

}
