// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sr;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.arkx.framework.data.db.common.entity.CloseableDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class StarRocksUtils {

  private String dbName;
  private String tbName;
  private String host;
  private String username;
  private String password;
  private CloseableDataSource dataSource;
  private String httpPort;

  public void init(String schemaName, String tableName, DataSource dataSource) {
    this.getHttpPort(dataSource);
    this.dataSource = (CloseableDataSource) dataSource;
    this.host = ReUtil.extractMulti("jdbc:mysql://(.*):[0-9]{2,8}/", this.dataSource.getJdbcUrl(), "$1");
    this.username = this.dataSource.getUserName();
    this.password = this.dataSource.getPassword();
    this.tbName = tableName;
    this.dbName = schemaName;
  }

  public void getHttpPort(DataSource dataSource) {
    Db use = Db.use(dataSource);
    try {
      List<Entity> frontends = use.query("SHOW FRONTENDS");
      List<FrontendEntity> frontendEntities = BeanUtil.copyToList(frontends, FrontendEntity.class);
      List<FrontendEntity> leader = frontendEntities.stream().filter(i -> i.getRole().equals("LEADER"))
          .collect(Collectors.toList());
      FrontendEntity frontendEntity = leader.getFirst();
      this.httpPort = frontendEntity.getHttpport();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public long addOrUpdateData(List<String> fieldNames, List<Object[]> recordValues) {
    if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
      return 0L;
    }
    List<Object> objectList = asObjectList(fieldNames, recordValues);
    JSONArray array = JSONUtil.parseArray(objectList);
    JSONObject jsonObject = JSONUtil.createObj().set("data", array);
    try {
      sendData(jsonObject.toString());
      return recordValues.size();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void sendData(String content) throws Exception {
    final String loadUrl = "http://%s:%s/api/%s/%s/_stream_load".formatted(
        this.host,
        this.httpPort,
        this.dbName,
        this.tbName);

    final HttpClientBuilder httpClientBuilder = HttpClients
        .custom()
        .setRedirectStrategy(new DefaultRedirectStrategy() {
//          @Override
          protected boolean isRedirectable(String method) {
            return true;
          }
        });
    try (CloseableHttpClient client = httpClientBuilder.build()) {
      HttpPut put = new HttpPut(loadUrl);
      StringEntity entity = new StringEntity(content, StandardCharsets.UTF_8);
      put.setHeader(HttpHeaders.EXPECT, "100-continue");
      put.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader(this.username, this.password));
      put.setHeader("strip_outer_array", "true");
      put.setHeader("format", "JSON");
      put.setHeader("json_root", "$.data");
      put.setHeader("ignore_json_size", "true");
      put.setHeader("Content-Type", "application/json");
      put.setEntity(entity);
      try (CloseableHttpResponse response = client.execute(put)) {
        String loadResult = "";
        if (response.getEntity() != null) {
          loadResult = EntityUtils.toString(response.getEntity());
        }
        final int statusCode = response.getCode();
        // statusCode 200 just indicates that starrocks be service is ok, not stream load
        // you should see the output content to find whether stream load is success
        if (statusCode != 200) {
          throw new IOException(
              "Stream load failed, statusCode=%s load result=%s".formatted(statusCode, loadResult));
        }
      }
    }
  }

  private String basicAuthHeader(String username, String password) {
    final String tobeEncode = username + ":" + password;
    byte[] encoded = Base64.getEncoder().encode(tobeEncode.getBytes(StandardCharsets.UTF_8));
    return "Basic " + new String(encoded);
  }

  private List<Object> asObjectList(List<String> fieldNames, List<Object[]> recordValues) {
    int fieldCount = Math.min(fieldNames.size(), recordValues.getFirst().length);
    List<Object> rows = new ArrayList<>(recordValues.size());
    for (Object[] row : recordValues) {
      Map<String, Object> columns = new LinkedHashMap<>(fieldCount);
      for (int i = 0; i < fieldCount; ++i) {
        Object rowValue = row[i];
        if (row[i] instanceof Timestamp) {
          rowValue = String.valueOf(rowValue);
        }
        columns.put(fieldNames.get(i), rowValue);
      }
      rows.add(columns);
    }
    return rows;
  }
}
