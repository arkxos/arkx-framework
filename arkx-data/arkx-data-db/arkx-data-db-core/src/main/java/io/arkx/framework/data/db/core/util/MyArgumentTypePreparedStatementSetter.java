package io.arkx.framework.data.db.core.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nobody
 * @date 2025-07-09 17:45
 * @since 1.0
 */
/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Simple adapter for {@link PreparedStatementSetter} that applies given arrays
 * of arguments and JDBC argument types.
 *
 * @author Juergen Hoeller
 * @since 3.2.3
 */
@Slf4j
public class MyArgumentTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private List<String> fieldOrders;
    private Map<String, String> columnTypes;
    @Nullable
    private final Object[] args;

    @Nullable
    private final int[] argTypes;

    /**
     * Create a new ArgTypePreparedStatementSetter for the given arguments.
     *
     * @param args
     *            the arguments to set
     * @param argTypes
     *            the corresponding SQL types of the arguments
     */
    public MyArgumentTypePreparedStatementSetter(List<String> fieldOrders, Map<String, String> columnTypes,
            @Nullable Object[] args, @Nullable int[] argTypes) {
        if ((args != null && argTypes == null) || (args == null && argTypes != null)
                || (args != null && args.length != argTypes.length)) {
            throw new InvalidDataAccessApiUsageException("args and argTypes parameters must match");
        }
        this.fieldOrders = fieldOrders;
        this.columnTypes = columnTypes;
        this.args = args;
        this.argTypes = argTypes;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        int parameterPosition = 1;
        if (this.args != null && this.argTypes != null) {
            for (int i = 0; i < this.args.length; i++) {
                Object arg = this.args[i];
                if (arg instanceof Collection<?> entries && this.argTypes[i] != Types.ARRAY) {
                    for (Object entry : entries) {
                        if (entry instanceof Object[] objects) {
                            Object[] valueArray = objects;
                            for (Object argValue : valueArray) {
                                doSetValue(ps, parameterPosition, this.argTypes[i], argValue);
                                parameterPosition++;
                            }
                        } else {
                            doSetValue(ps, parameterPosition, this.argTypes[i], entry);
                            parameterPosition++;
                        }
                    }
                } else {
                    try {
                        // timestamp
                        if (argTypes[i] == 93) {
                            if ("".equals(arg)) {
                                arg = null;
                            }
                        }
                        // 过滤掉undefined
                        if ("undefined".equals(arg)) {
                            arg = 0;
                            System.out.println("arg = " + arg);
                        }
                        // 如果目的数据类型是数字类型，并且当前值是字符串，则进行处理
                        if (argTypes[i] == -5 && arg instanceof String) {
                            arg = EmptyUtils.removeSpace(arg);
                        }
                        doSetValue(ps, parameterPosition, this.argTypes[i], arg);
                    } catch (Exception e) {
                        System.err.println("sql参数不正确，columnName: [" + fieldOrders.get(i) + "]，" + "columnType: ["
                                + columnTypes.get(fieldOrders.get(i)) + "]，" + "value: [" + arg + "] ，error："
                                + e.getMessage());
                        throw e;
                    }
                    parameterPosition++;
                }
            }
        }
    }

    /**
     * Set the value for the prepared statement's specified parameter position using
     * the passed in value and type. This method can be overridden by sub-classes if
     * needed.
     *
     * @param ps
     *            the PreparedStatement
     * @param parameterPosition
     *            index of the parameter position
     * @param argType
     *            the argument type
     * @param argValue
     *            the argument value
     * @throws SQLException
     *             if thrown by PreparedStatement methods
     */
    protected void doSetValue(PreparedStatement ps, int parameterPosition, int argType, Object argValue)
            throws SQLException {

        StatementCreatorUtils.setParameterValue(ps, parameterPosition, argType, argValue);
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.args);
    }

}
