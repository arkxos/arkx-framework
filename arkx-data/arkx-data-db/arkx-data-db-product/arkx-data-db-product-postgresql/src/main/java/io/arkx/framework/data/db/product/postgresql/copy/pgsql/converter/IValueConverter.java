package io.arkx.framework.data.db.product.postgresql.copy.pgsql.converter;

public interface IValueConverter<TSource, TTarget> {

	TTarget convert(TSource source);

}
