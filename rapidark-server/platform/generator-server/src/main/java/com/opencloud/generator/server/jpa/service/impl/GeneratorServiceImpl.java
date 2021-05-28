/*
 *  Copyright 2019-2021 RapidArk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.opencloud.generator.server.jpa.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import com.opencloud.generator.server.jpa.entity.GenConfig;
import com.opencloud.generator.server.jpa.entity.ColumnInfo;
import com.opencloud.generator.server.jpa.entity.vo.TableInfo;
import me.zhengjie.exception.BadRequestException;
import com.opencloud.generator.server.jpa.repository.ColumnInfoRepository;
import com.opencloud.generator.server.jpa.service.GeneratorService;
import me.zhengjie.utils.FileUtil;
import com.opencloud.generator.server.jpa.util.GenUtil;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2019-01-02
 */
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

    private static final Logger log = LoggerFactory.getLogger(GeneratorServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    private final ColumnInfoRepository columnInfoRepository;

    /**
     * 是否为Oracle数据库
     * @return true:oracle,false:mysql或sql server
     */
    public boolean isOracleDataBase(){
        boolean res = false;
        EntityManagerFactory entityManagerFactory = em.getEntityManagerFactory();
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl)entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        if (session != null) {
            res = session.doReturningWork(
                    connection -> {
                        String dbName = connection.getMetaData().getDatabaseProductName();
                        System.out.println("dbName: " + dbName);
                        if(StringUtils.equals("Oracle",dbName)){
                            return true;
                        } else {
                            return false;
                        }
                    }
            );
            session.close();
        }
        return res;
    }

    @Override
    public Object getTables() {
        boolean isOracle = isOracleDataBase();
        // 使用预编译防止sql注入
        String sql = "select table_name ,create_time , engine, table_collation, table_comment from information_schema.tables " +
                "where table_schema = (select database()) " +
                "order by create_time desc";
        Query query = em.createNativeQuery(sql);
        return query.getResultList();
    }

    @Override
    public Object getTables(String name, int[] startEnd) {
        boolean isOracle = isOracleDataBase();
        // 使用预编译防止sql注入
        String sql = "";
        if (isOracle) {
            sql = "select t.table_name ,uo.CREATED AS create_time , 'pdb' AS engine, 'utf8' AS table_collation, f.comments AS table_comment\n" +
                    "  from user_tables t\n" +
                    " inner join user_tab_comments f on t.table_name = f.table_name\n" +
                    " LEFT JOIN user_objects uo ON t.table_name = uo.object_name" +
                    " WHERE  " +
                    " t.table_name like ? order by uo.CREATED desc, t.table_name asc";
        } else {
            sql = "select table_name ,create_time , engine, table_collation, table_comment from information_schema.tables " +
                    "where table_schema = (select database()) " +
                    "and table_name like ? order by create_time desc";
        }

        Query query = em.createNativeQuery(sql);
        query.setFirstResult(startEnd[0]);
        query.setMaxResults(startEnd[1] - startEnd[0]);
        query.setParameter(1, StringUtils.isNotBlank(name) ? ("%" + name + "%") : "%%");
        List result = query.getResultList();
        List<TableInfo> tableInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            tableInfos.add(new TableInfo(arr[0], arr[1], arr[2], arr[3], ObjectUtil.isNotEmpty(arr[4]) ? arr[4] : "-"));
        }
        Object totalElements = 0;
        if (isOracle) {
            Query query1 = em.createNativeQuery("SELECT COUNT(1) FROM (\n" +
                    "\tselect t.table_name ,uo.CREATED AS create_time , 'pdb' AS engine, 'utf8' AS table_collation, f.comments AS table_comment\n" +
                    "\t\tfrom user_tables t\n" +
                    "\t inner join user_tab_comments f on t.table_name = f.table_name\n" +
                    " LEFT JOIN user_objects uo ON t.table_name = uo.object_name" +
                    "\t WHERE t.table_name like ?\n" +
                    " )");
            query1.setParameter(1, StringUtils.isNotBlank(name) ? ("%" + name + "%") : "%%");
            totalElements = query1.getSingleResult();
        } else {
            Query query1 = em.createNativeQuery("SELECT COUNT(*) from information_schema.tables where table_schema = (select database())");
            totalElements = query1.getSingleResult();
        }

        return PageUtil.toPage(tableInfos, totalElements);
    }

    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        List<ColumnInfo> columnInfos = columnInfoRepository.findByTableNameOrderByIdAsc(tableName);
        if (CollectionUtil.isNotEmpty(columnInfos)) {
            return columnInfos;
        } else {
            columnInfos = query(tableName);
            return columnInfoRepository.saveAll(columnInfos);
        }
    }

    @Override
    public List<ColumnInfo> query(String tableName) {
        // 使用预编译防止sql注入
        String sql = "";
        if (isOracleDataBase()) {
            sql = "SELECT\n" +
                    "\tutc.column_name AS column_name,\n" +
                    "\tCASE utc.nullable WHEN 'N' THEN 'NO' ELSE 'YES' END is_nullable,\n" +
                    "\tutc.data_type AS data_type,\n" +
                    "\tucc.comments column_comment,\n" +
                    "\tCASE UTC.COLUMN_NAME  WHEN (\n" +
                    "\t\tSELECT\n" +
                    "\t\t\tcol.column_name \n" +
                    "\t\tFROM\n" +
                    "\t\t\tuser_constraints con,\n" +
                    "\t\t\tuser_cons_columns col \n" +
                    "\t\tWHERE\n" +
                    "\t\t\tcon.constraint_name = col.constraint_name \n" +
                    "\t\t\tAND con.constraint_type = 'P' \n" +
                    "\t\t\tAND col.table_name = ? \n" +
                    "\t\t) THEN 'PRI' ELSE '' END AS column_key ,\n" +
                    "\t'' as extra\n" +
                    "\t--utc.data_length AS 最大长度,\n" +
                    "\t--utc.data_default 默认值,\n" +
                    "\t--UTC.table_name 表名\n" +
                    "\n" +
                    "FROM\n" +
                    "\tuser_tab_columns utc,\n" +
                    "\tuser_col_comments ucc \n" +
                    "WHERE\n" +
                    "\tutc.table_name = ucc.table_name \n" +
                    "\tAND utc.column_name = ucc.column_name \n" +
                    "\tAND utc.table_name = ?";
        } else {
            sql = "select column_name, is_nullable, data_type, column_comment, column_key, extra from information_schema.columns " +
                    "where table_name = ? and table_schema = (select database()) order by ordinal_position";
        }
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, tableName);
        if (isOracleDataBase()) {
            query.setParameter(2, tableName);
        }
        List result = query.getResultList();
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            columnInfos.add(
                    new ColumnInfo(
                            tableName,
                            arr[0].toString(),
                            "NO".equals(arr[1]),
                            arr[2].toString(),
                            ObjectUtil.isNotNull(arr[3]) ? arr[3].toString() : null,
                            ObjectUtil.isNotNull(arr[4]) ? arr[4].toString() : null,
                            ObjectUtil.isNotNull(arr[5]) ? arr[5].toString() : null)
            );
        }
        return columnInfos;
    }

    @Override
    public void sync(List<ColumnInfo> columnInfos, List<ColumnInfo> columnInfoList) {
        // 第一种情况，数据库类字段改变或者新增字段
        for (ColumnInfo columnInfo : columnInfoList) {
            // 根据字段名称查找
            List<ColumnInfo> columns = columnInfos.stream().filter(c -> c.getColumnName().equals(columnInfo.getColumnName())).collect(Collectors.toList());
            // 如果能找到，就修改部分可能被字段
            if (CollectionUtil.isNotEmpty(columns)) {
                ColumnInfo column = columns.get(0);
                column.setColumnType(columnInfo.getColumnType());
                column.setExtra(columnInfo.getExtra());
                column.setKeyType(columnInfo.getKeyType());
                if (StringUtils.isBlank(column.getRemark())) {
                    column.setRemark(columnInfo.getRemark());
                }
                columnInfoRepository.save(column);
            } else {
                // 如果找不到，则保存新字段信息
                columnInfoRepository.save(columnInfo);
            }
        }
        // 第二种情况，数据库字段删除了
        for (ColumnInfo columnInfo : columnInfos) {
            // 根据字段名称查找
            List<ColumnInfo> columns = columnInfoList.stream().filter(c -> c.getColumnName().equals(columnInfo.getColumnName())).collect(Collectors.toList());
            // 如果找不到，就代表字段被删除了，则需要删除该字段
            if (CollectionUtil.isEmpty(columns)) {
                columnInfoRepository.delete(columnInfo);
            }
        }
    }

    @Override
    public void save(List<ColumnInfo> columnInfos) {
        columnInfoRepository.saveAll(columnInfos);
    }

    @Override
    public void generator(GenConfig genConfig, List<ColumnInfo> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        try {
            GenUtil.generatorCode(columns, genConfig);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("生成失败，请手动处理已生成的文件");
        }
    }

    @Override
    public ResponseEntity<Object> preview(GenConfig genConfig, List<ColumnInfo> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        List<Map<String, Object>> genList = GenUtil.preview(columns, genConfig);
        return new ResponseEntity<>(genList, HttpStatus.OK);
    }

    @Override
    public void download(GenConfig genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        try {
            File file = new File(GenUtil.download(columns, genConfig));
            String zipPath = file.getPath() + ".zip";
            ZipUtil.zip(file.getPath(), zipPath);
            FileUtil.downloadFile(request, response, new File(zipPath), true);
        } catch (IOException e) {
            throw new BadRequestException("打包失败");
        }
    }
}
