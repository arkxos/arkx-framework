/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.arkx.framework.datasource.dynamic.support;

/**
 * @author lengleng
 * @date 2019-04-01
 *       <p>
 *       数据源相关常量
 */
public interface DataSourceConstants {

    /**
     * 数据源名称
     */
    String NAME = "name";

    /**
     * 默认数据源（master）
     */
    String DS_MASTER = "master";

    /**
     * jdbcurl
     */
    String DS_JDBC_URL = "url";

    /**
     * 配置类型
     */
    String DS_CONFIG_TYPE = "conf_type";

    /**
     * 用户名
     */
    String DS_USER_NAME = "username";

    /**
     * 密码
     */
    String DS_USER_PWD = "password";

    /**
     * 驱动包名称
     */
    String DS_DRIVER_CLASS_NAME = "driver_class_name";

    /**
     * 数据库类型
     */
    String DS_TYPE = "ds_type";

    /**
     * 数据库名称
     */
    String DS_NAME = "ds_name";

    /**
     * 主机类型
     */
    String DS_HOST = "host";

    /**
     * 端口
     */
    String DS_PORT = "port";

    /**
     * 实例名称
     */
    String DS_INSTANCE = "instance";

}
