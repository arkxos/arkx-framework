package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.constants.DataType;

public interface IValueHandlerProvider {

    <TTargetType> IValueHandler<TTargetType> resolve(DataType targetType);
}
