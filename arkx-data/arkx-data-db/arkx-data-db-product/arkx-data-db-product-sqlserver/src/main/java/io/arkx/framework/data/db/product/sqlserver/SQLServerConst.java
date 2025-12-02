// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sqlserver;

public final class SQLServerConst {

    public static final String GET_CURRENT_CATALOG_SQL = "Select Name From Master..SysDataBases Where DbId=(Select Dbid From Master..SysProcesses Where Spid = @@spid)";

    /**
     * 删除临时表
     */
    public static final String DROP_TEMPTABLE_SQL = "IF (OBJECT_ID('tempdb.dbo.#t') IS NOT NULL) DROP TABLE #t";

    /**
     * 创建临时表
     */
    public static final String CREATE_TEMPTABLE_SQL = """
            DECLARE @schemaname VARCHAR(1024)
            DECLARE @tabname VARCHAR(1024)
            SET @schemaname = '%s'
            SET @tabname = '%s'
            SELECT  'CREATE TABLE [' + DB_NAME() + '].[' + @schemaname + '].[' + so.name + '] (' + o.list + ')'\s
             + CASE WHEN tc.Constraint_Name IS NULL THEN '' ELSE 'ALTER TABLE [' + DB_NAME() + '].[' + @schemaname + '].[' + so.Name + '] ADD CONSTRAINT ' + tc.Constraint_Name  + ' PRIMARY KEY ' + ' (' + LEFT(j.List, LEN(j.List)-1) + ')' END\s
             AS TABLE_DDL\s
             INTO #t\s
             FROM sysobjects so\s
             CROSS APPLY
             (SELECT\s
                '  [' + column_name + '] ' +\s
                data_type + CASE data_type
                    WHEN 'sql_variant' THEN ''
                    WHEN 'text' THEN ''
                    WHEN 'ntext' THEN ''
                    WHEN 'xml' THEN ''
                    WHEN 'decimal' THEN '(' + CAST(numeric_precision AS VARCHAR) + ', ' + CAST(numeric_scale AS VARCHAR) + ')'
                    ELSE COALESCE('(' + CASE WHEN character_maximum_length = -1 THEN 'MAX' ELSE CAST(character_maximum_length AS VARCHAR) END + ')', '') END + ' ' +
                CASE WHEN EXISTS (\s
                    SELECT id FROM syscolumns
                    WHERE OBJECT_NAME(id) = so.name
                    AND name = column_name
                    AND COLUMNPROPERTY(id, name, 'IsIdentity') = 1\s
                ) THEN
                    'IDENTITY(' +\s
                    CAST(ident_seed(so.name) AS VARCHAR) + ',' +\s
                    CAST(ident_incr(so.name) AS VARCHAR) + ')'
                ELSE ''
                END + ' ' +
                (CASE WHEN IS_NULLABLE = 'No' THEN 'NOT ' ELSE '' END) + 'NULL ' +\s
                CASE WHEN information_schema.columns.COLUMN_DEFAULT IS NOT NULL THEN 'DEFAULT ' + information_schema.columns.COLUMN_DEFAULT ELSE '' END + ', '\s
             FROM information_schema.columns WHERE table_schema = @schemaname AND table_name = so.name
             ORDER BY ordinal_position
             FOR XML PATH('')) o (list)
             LEFT JOIN
                 information_schema.table_constraints tc
             ON  tc.Table_name       = so.Name
             AND tc.Constraint_Type  = 'PRIMARY KEY'
             CROSS APPLY
             (SELECT '[' + Column_Name + '], '
             FROM   information_schema.key_column_usage kcu
             WHERE  kcu.Constraint_Name = tc.Constraint_Name
             ORDER BY
                ORDINAL_POSITION
             FOR XML PATH('')) j (list)
             WHERE   xtype = 'U'
             AND name = @tabname;\
            """;

    /**
     * 获取ddl
     */
    public static final String SELECT_DDL_SQL = """
            SELECT (
             CASE WHEN (
                 SELECT COUNT(a.constraint_type)
                 FROM information_schema.table_constraints a\s
                 INNER JOIN information_schema.constraint_column_usage b
                 ON a.constraint_name = b.constraint_name
                 WHERE a.constraint_type = 'PRIMARY KEY'\s
                 AND a.CONSTRAINT_SCHEMA = '%s'
                 AND a.table_name = '%s'
             ) = 1 THEN
                 REPLACE(table_ddl, ', )ALTER TABLE', ') ALTER TABLE')
             ELSE\s
                 SUBSTRING(table_ddl, 1, LEN(table_ddl) - 3) + ')'
             END
            ) AS createTableStatement
             FROM #t;\
            """;

    private SQLServerConst() {

    }
}
