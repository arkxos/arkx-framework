# Maven 仓库使用指南

## 📊 仓库对比

| 仓库 | 可用性 | 同步时间 | GPG签名 | 访问限制 | 推荐场景 |
|------|--------|----------|---------|----------|----------|
| **GitHub Packages** | ⚡ 立即 | 实时 | 可选 | 需要 GitHub Token | 开发测试、内部使用 |
| **Sonatype Snapshots** | ⚡ 立即 | 实时 | 必需 | 无限制 | 快照版本测试 |
| **Maven Central** | ⏰ 延迟 | 2-4小时 | 必需 | 无限制 | 生产环境、正式发布 |

## 🚀 快速开始

### 1. 立即可用（推荐用于开发测试）

#### GitHub Packages（立即可用）
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/arkxos/arkx-framework</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

#### Sonatype Snapshots（立即可用）
```xml
<repositories>
    <repository>
        <id>sonatype-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 2. 生产环境（Maven Central）

#### Maven Central（2-4小时同步）
```xml
<!-- 无需额外配置，Maven Central 是默认仓库 -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.0</version>
</dependency>
```

## 📦 依赖配置

### 完整配置示例

#### 立即可用配置（GitHub + Sonatype）
```xml
<project>
    <repositories>
        <!-- GitHub Packages - 立即可用 -->
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/arkxos/arkx-framework</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        
        <!-- Sonatype Snapshots - 立即可用 -->
        <repository>
            <id>sonatype-snapshots</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
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
</project>
```

#### 生产环境配置（Maven Central）
```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.arkx.framework</groupId>
            <artifactId>arkx-framework</artifactId>
            <version>0.3.0</version>
        </dependency>
    </dependencies>
</project>
```

## 🔧 认证配置

### GitHub Packages 认证

在 `~/.m2/settings.xml` 中配置：
```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

**获取 GitHub Token：**
1. 访问 https://github.com/settings/tokens
2. 生成新的 Personal Access Token
3. 选择 `read:packages` 权限

### Sonatype 认证

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>YOUR_SONATYPE_USERNAME</username>
            <password>YOUR_SONATYPE_PASSWORD</password>
        </server>
    </servers>
</settings>
```

## 📋 版本策略

### 版本类型

| 版本类型 | 格式示例 | 发布仓库 | 可用时间 |
|----------|----------|----------|----------|
| **Release** | `0.3.0`, `1.0.0` | GitHub + Central | GitHub: 立即, Central: 2-4小时 |
| **Snapshot** | `0.3.1-SNAPSHOT` | GitHub + Sonatype | 立即 |
| **RC** | `0.3.0-RC1` | GitHub + Central | GitHub: 立即, Central: 2-4小时 |

### 使用建议

```xml
<!-- 开发阶段：使用 Snapshot -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.1-SNAPSHOT</version>
</dependency>

<!-- 测试阶段：使用 GitHub Packages Release -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.0-RC1</version>
</dependency>

<!-- 生产环境：使用 Maven Central -->
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.3.0</version>
</dependency>
```

## 🔍 验证和检查

### 1. 检查版本可用性

#### GitHub Packages
```bash
curl -H "Authorization: token YOUR_TOKEN" \
     https://maven.pkg.github.com/arkxos/arkx-framework/io/arkx/framework/arkx-framework/
```

#### Sonatype Snapshots
```bash
curl https://oss.sonatype.org/content/repositories/snapshots/io/arkx/framework/arkx-framework/
```

#### Maven Central
```bash
curl https://search.maven.org/solrsearch/select?q=g:io.arkx.framework+AND+a:arkx-framework
```

### 2. Maven 依赖检查

```bash
# 检查依赖解析
mvn dependency:resolve -Dclassifier=sources

# 查看依赖树
mvn dependency:tree

# 下载源码
mvn dependency:sources
```

## 📊 性能对比

### 下载速度对比

| 仓库 | 平均下载速度 | 稳定性 | CDN支持 |
|------|--------------|--------|---------|
| GitHub Packages | 🟢 快 | 🟢 高 | 🟢 全球CDN |
| Sonatype Snapshots | 🟡 中等 | 🟢 高 | 🟡 有限 |
| Maven Central | 🟢 快 | 🟢 高 | 🟢 全球CDN |

### 推荐使用策略

```xml
<!-- 开发环境：优先使用 GitHub Packages -->
<profiles>
    <profile>
        <id>development</id>
        <repositories>
            <repository>
                <id>github</id>
                <url>https://maven.pkg.github.com/arkxos/arkx-framework</url>
            </repository>
        </repositories>
    </profile>
    
    <!-- 生产环境：使用 Maven Central -->
    <profile>
        <id>production</id>
        <!-- 使用默认的 Maven Central -->
    </profile>
</profiles>
```

## 🚨 注意事项

### 1. 版本同步延迟

- **Maven Central**: 发布后需要 2-4 小时同步
- **GitHub Packages**: 立即可用
- **Sonatype Snapshots**: 立即可用

### 2. 认证要求

- **GitHub Packages**: 需要 GitHub Token
- **Sonatype**: 需要 Sonatype 账号（仅发布时）
- **Maven Central**: 无需认证（仅消费）

### 3. GPG 签名

- **Maven Central**: 必需 GPG 签名
- **GitHub Packages**: 可选
- **Sonatype Snapshots**: 必需 GPG 签名

## 📞 故障排除

### 常见问题

1. **GitHub Packages 401 错误**
   - 检查 Token 权限
   - 确认 Token 未过期
   - 验证 settings.xml 配置

2. **Maven Central 找不到版本**
   - 等待 2-4 小时同步
   - 检查版本号格式
   - 确认发布流程成功

3. **依赖解析失败**
   - 检查仓库 URL
   - 验证网络连接
   - 清理 Maven 缓存：`mvn clean`

### 获取帮助

- 📧 技术支持：team@arkx.io
- 🐛 问题反馈：https://github.com/arkxos/arkx-framework/issues
- 📖 文档：https://www.arkx.io/docs

---

💡 **提示**: 建议开发阶段使用 GitHub Packages 获得即时更新，生产环境使用 Maven Central 确保稳定性。