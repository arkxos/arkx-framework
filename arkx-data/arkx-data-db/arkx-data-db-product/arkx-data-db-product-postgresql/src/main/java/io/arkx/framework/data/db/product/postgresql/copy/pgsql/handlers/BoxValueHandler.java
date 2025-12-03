package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers.utils.GeometricUtils;
import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.geometric.Box;

public class BoxValueHandler extends BaseValueHandler<Box> {

    @Override
    protected void internalHandle(DataOutputStream buffer, final Box value) throws IOException {
        buffer.writeInt(32);

        GeometricUtils.writePoint(buffer, value.getHigh());
        GeometricUtils.writePoint(buffer, value.getLow());
    }

    @Override
    public int getLength(Box value) {
        return 32;
    }

}
