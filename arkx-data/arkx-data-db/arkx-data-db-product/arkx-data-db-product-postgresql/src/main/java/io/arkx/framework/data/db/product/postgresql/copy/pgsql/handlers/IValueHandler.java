package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import java.io.DataOutputStream;

public interface IValueHandler<TTargetType> extends ValueHandler {

    void handle(DataOutputStream buffer, final TTargetType value);

    int getLength(final TTargetType value);
}
