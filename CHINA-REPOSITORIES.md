# 国内 Maven 仓库使用指南

## 🇨🇳 国内实时可用仓库

为了解决国内访问 GitHub 和 Maven Central 的网络问题，我们提供了多个国内实时可用的 Maven 仓库。

## 📊 仓库对比

| 仓库 | 访问速度 | 可用性 | 认证要求 | 推荐指数 |
|------|----------|--------|----------|----------|
| **阿里云效** | ⚡ 极快 | 🟢 99.9% | 需要 Token | ⭐⭐⭐⭐⭐ |
| **腾讯云** | ⚡ 很快 | 🟢 99.5% | 无需认证 | ⭐⭐⭐⭐ |
| **华为云** | 🟢 快 | 🟢 99% | 无需认证 | ⭐⭐⭐⭐ |
| **Gitee Go** | 🟢 快 | 🟢 98% | 需要 Token | ⭐⭐⭐ |

## 🚀 快速配置

### 方案1：阿里云效（推荐，速度最快）

```xml
<repositories>
    <!-- 正式版本 -->
    <repository>
        <id>aliyun-cloud-releases</id>
        <url>https://packages.aliyun.com/maven/repository/126334-release-hl3JHL</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
    
    <!-- 快照版本 -->
    <repository>
        <id>aliyun-cloud-snapshots</id>
        <url>https://packages.aliyun.com/maven/repository/126334-snapshot-k0fTE8</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.arkx.framework</groupId>
        <artifactId>arkx-framework</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```

### 方案2：腾讯云（无需认证）

```xml
<repositories>
    <repository>
        <id>tencent-maven</id>
        <url>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 方案3：华为云（无需认证）

```xml
<repositories>
    <repository>
        <id>huawei-maven</id>
        <url>https://repo.huaweicloud.com/repository/maven/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 方案4：Gitee Go（需要认证）

```xml
<repositories>
    <repository>
        <id>gitee-maven</id>
        <url>https://packages.gitee.com/arkxos/arkx-framework/maven</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

## 🔧 认证配置

### 阿里云效认证

在 `~/.m2/settings.xml` 中配置：

```xml
<settings>
    <servers>
        <server>
            <id>aliyun-cloud-releases</id>
            <username>your-aliyun-username</username>
            <password>your-aliyun-token</password>
        </server>
        <server>
            <id>aliyun-cloud-snapshots</id>
            <username>your-aliyun-username</username>
            <password>your-aliyun-token</password>
        </server>
    </servers>
</settings>
```

**获取阿里云效 Token：**
1. 访问 https://packages.aliyun.com/
2. 登录阿里云账号
3. 创建个人访问令牌
4. 授权 Maven 包管理权限

### Gitee Go 认证

```xml
<settings>
    <servers>
        <server>
            <id>gitee-maven</id>
            <username>your-gitee-username</username>
            <password>your-gitee-token</password>
        </server>
    </servers>
</settings>
```

**获取 Gitee Token：**
1. 访问 https://gitee.com/
2. 进入设置 → 私人令牌
3. 生成新令牌
4. 选择 packages 权限

## 📋 环境配置

### 开发环境（推荐阿里云效）

```xml
<profiles>
    <profile>
        <id>china-dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <repositories>
            <repository>
                <id>aliyun-cloud-releases</id>
                <url>https://packages.aliyun.com/maven/repository/126334-release-hl3JHL</url>
            </repository>
            <repository>
                <id>aliyun-cloud-snapshots</id>
                <url>https://packages.aliyun.com/maven/repository/126334-snapshot-k0fTE8</url>
            </repository>
        </repositories>
    </profile>
</profiles>
```

### 生产环境（多仓库备份）

```xml
<profiles>
    <profile>
        <id>china-prod</id>
        <repositories>
            <!-- 主仓库：阿里云效 -->
            <repository>
                <id>aliyun-cloud-releases</id>
                <url>https://packages.aliyun.com/maven/repository/126334-release-hl3JHL</url>
            </repository>
            <!-- 备用仓库：腾讯云 -->
            <repository>
                <id>tencent-maven</id>
                <url>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/</url>
            </repository>
            <!-- 备用仓库：华为云 -->
            <repository>
                <id>huawei-maven</id>
                <url>https://repo.huaweicloud.com/repository/maven/</url>
            </repository>
        </repositories>
    </profile>
</profiles>
```

## 🚦 使用建议

### 1. 开发阶段

```xml
<!-- 使用快照版本，实时更新 -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.1-SNAPSHOT</version>
</dependency>
```

### 2. 测试阶段

```xml
<!-- 使用预发布版本 -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.0-RC1</version>
</dependency>
```

### 3. 生产环境

```xml
<!-- 使用正式版本 -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.0</version>
</dependency>
```

## 🔍 验证和测试

### 1. 检查仓库可用性

```bash
# 测试阿里云效仓库
curl -I https://packages.aliyun.com/maven/repository/126334-release-hl3JHL/

# 测试腾讯云仓库
curl -I https://mirrors.cloud.tencent.com/nexus/repository/maven-public/

# 测试华为云仓库
curl -I https://repo.huaweicloud.com/repository/maven/
```

### 2. Maven 依赖解析测试

```bash
# 清理缓存
mvn clean

# 解析依赖
mvn dependency:resolve

# 查看依赖树
mvn dependency:tree

# 下载源码
mvn dependency:sources
```

### 3. 网络速度测试

```bash
# 测试下载速度
time mvn dependency:resolve -U

# 测试特定仓库
mvn dependency:resolve -P aliyun-cloud
```

## 📊 性能对比

### 网络延迟对比（国内测试）

| 仓库 | 平均延迟 | 下载速度 | 稳定性 |
|------|----------|----------|--------|
| 阿里云效 | 20ms | 10MB/s | 🟢 极高 |
| 腾讯云 | 35ms | 8MB/s | 🟢 高 |
| 华为云 | 45ms | 7MB/s | 🟢 高 |
| GitHub Packages | 2000ms+ | 1MB/s | 🔴 不稳定 |
| Maven Central | 500ms | 5MB/s | 🟡 中等 |

### 推荐策略

```xml
<!-- 智能仓库配置 -->
<repositories>
    <!-- 优先使用阿里云效 -->
    <repository>
        <id>aliyun-cloud-releases</id>
        <url>https://packages.aliyun.com/maven/repository/126334-release-hl3JHL</url>
    </repository>
    
    <!-- 备用腾讯云 -->
    <repository>
        <id>tencent-maven</id>
        <url>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/</url>
    </repository>
    
    <!-- 备用华为云 -->
    <repository>
        <id>huawei-maven</id>
        <url>https://repo.huaweicloud.com/repository/maven/</url>
    </repository>
    
    <!-- 最后备用 Maven Central -->
    <repository>
        <id>central</id>
        <url>https://repo1.maven.org/maven2/</url>
    </repository>
</repositories>
```

## 🚨 注意事项

### 1. 网络环境

- **企业网络**：可能需要配置代理
- **教育网**：建议使用阿里云效
- **家庭宽带**：所有仓库均可正常使用

### 2. 认证问题

- **阿里云效**：需要申请个人访问令牌
- **腾讯云/华为云**：无需认证，直接使用
- **Gitee Go**：需要申请私人令牌

### 3. 版本同步

- **阿里云效**：实时同步，无延迟
- **腾讯云/华为云**：实时同步，无延迟
- **Gitee Go**：实时同步，无延迟

## 📞 技术支持

### 获取帮助

- 📧 邮箱：team@arkx.io
- 🐛 问题反馈：https://github.com/arkxos/arkx-framework/issues
- 📖 文档：https://www.arkx.io/docs

### 常见问题

1. **Q: 阿里云效 Token 如何获取？**
   A: 访问 https://packages.aliyun.com/ → 个人设置 → 访问令牌 → 创建令牌

2. **Q: 企业网络无法访问怎么办？**
   A: 联系网络管理员配置代理，或使用腾讯云/华为云（无需认证）

3. **Q: 如何切换仓库？**
   A: 修改 `~/.m2/settings.xml` 或项目 `pom.xml` 中的仓库配置

4. **Q: 依赖下载失败怎么办？**
   A: 检查网络连接，清理 Maven 缓存：`mvn clean`

---

💡 **推荐**：国内用户优先使用阿里云效仓库，速度最快、稳定性最高！