package com.arkxos.framework.data.simplejdbc.query;

import java.util.Collection;

public interface Criterion {

	Object getValue();

	Collection<?> getValues();

	String toSqlString();
}