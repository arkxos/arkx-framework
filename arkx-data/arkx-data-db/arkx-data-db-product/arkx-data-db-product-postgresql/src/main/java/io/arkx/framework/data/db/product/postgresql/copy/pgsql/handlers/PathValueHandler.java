package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers.utils.GeometricUtils;
import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.geometric.Path;
import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.geometric.Point;

public class PathValueHandler extends BaseValueHandler<Path> {

    @Override
    protected void internalHandle(DataOutputStream buffer, final Path value) throws IOException {
        // Write a Byte to indicate if a Path is closed or not:
        byte pathIsClosed = (byte) (value.isClosed() ? 1 : 0);

        // The total number of bytes to write:
        int totalBytesToWrite = 1 + 4 + 16 * value.size();

        // The Number of Bytes to follow:
        buffer.writeInt(totalBytesToWrite);
        // Is the Circle close?
        buffer.writeByte(pathIsClosed);
        // Write Points:
        buffer.writeInt(value.getPoints().size());
        // Write each Point in List:
        for (Point p : value.getPoints()) {
            GeometricUtils.writePoint(buffer, p);
        }

    }

    @Override
    public int getLength(Path value) {
        throw new UnsupportedOperationException();
    }
}
