package io.arkx.framework.data.db.product.postgresql.copy.pgsql.handlers;

import java.io.DataOutputStream;
import java.io.IOException;

import io.arkx.framework.data.db.product.postgresql.copy.pgsql.model.geometric.Line;

public class LineValueHandler extends BaseValueHandler<Line> {

	@Override
	protected void internalHandle(DataOutputStream buffer, final Line value) throws IOException {
		buffer.writeInt(24);

		buffer.writeDouble(value.getA());
		buffer.writeDouble(value.getB());
		buffer.writeDouble(value.getC());
	}

	@Override
	public int getLength(Line value) {
		return 24;
	}

}
