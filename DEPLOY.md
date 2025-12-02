# Maven Central 自动发布配置使用说明

## 概述

本项目已配置了完整的 Maven Central 自动发布功能，支持发布到 Maven Central Repository。

## 支持的仓库类型

### Maven Central 仓库
- **Profile ID**: `central-release`
- **适用场景**: 开源项目发布到 Maven Central
- **仓库地址**: https://central.sonatype.com

## 使用方法

### 1. 发布到 Nexus 私有仓库

```bash
# 发布 SNAPSHOT 版本
mvn clean deploy -P nexus-release

# 发布 RELEASE 版本
mvn clean deploy -P nexus-release,release
```

### 2. 发布到 Maven Central 仓库

```bash
# 发布 SNAPSHOT 版本
mvn clean deploy -P central-release

# 发布 RELEASE 版本
mvn clean deploy -P central-release,release -Dgpg.passphrase=your-gpg-password
```

## 前置配置

### 1. Maven Settings.xml 配置

在 `~/.m2/settings.xml` 中添加仓库认证信息：

```xml
<settings>
    <servers>
        <!-- Nexus 仓库认证 -->
        <server>
            <id>nexus-releases</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
        <server>
            <id>nexus-snapshots</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
        

        <!-- Maven Central 仓库认证 -->
        <server>
            <id>ossrh</id>
            <username>your-sonatype-username</username>
            <password>your-sonatype-password</password>
        </server>
    </servers>
</settings>
```

### 2. GPG 密钥配置（仅 Maven Central 需要）

```bash
# 生成 GPG 密钥
gpg --gen-key

# 列出密钥
gpg --list-keys

# 导出公钥到密钥服务器
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID

# 查看 GPG 密钥 ID
gpg --list-secret-keys --keyid-format LONG
```

## Profile 说明

### release Profile
`release` profile 包含以下功能：
- 自动生成源码包（source jar）
- 自动生成 Javadoc 文档
- 自动进行 GPG 签名
- 配置 Maven Deploy 插件

### 版本管理
项目使用 `${revision}` 属性进行版本管理，当前版本为 `0.3.0`。

修改版本号：
```bash
# 修改 pom.xml 中的 revision 属性
# 或者使用 Maven Release 插件
mvn release:prepare
mvn release:perform
```

## 常见问题

### 1. GPG 签名失败
```bash
# 解决方案：设置 GPG 密码环境变量
export GPG_TTY=$(tty)
# 或者在命令中指定密码
mvn deploy -P central-release,release -Dgpg.passphrase=your-password
```

### 2. 401 认证失败
检查 `settings.xml` 中的用户名和密码是否正确。

### 3. 网络连接问题
确保能够访问目标 Maven 仓库地址。

## 自动化脚本示例

### 发布脚本示例

```bash
#!/bin/bash
# deploy.sh

REPO_TYPE=$1
VERSION_TYPE=$2

if [ -z "$REPO_TYPE" ]; then
    echo "Usage: ./deploy.sh <repo-type> [version-type]"
    echo "repo-type: nexus, aliyun, central"
    echo "version-type: snapshot, release (default: snapshot)"
    exit 1
fi

if [ -z "$VERSION_TYPE" ]; then
    VERSION_TYPE="snapshot"
fi

case $REPO_TYPE in
    "nexus")
        PROFILE="nexus-release"
        ;;
    "aliyun")
        PROFILE="aliyun-rdc-release"
        ;;
    "central")
        PROFILE="central-release"
        ;;
    *)
        echo "Unsupported repo type: $REPO_TYPE"
        exit 1
        ;;
esac

if [ "$VERSION_TYPE" = "release" ]; then
    mvn clean deploy -P $PROFILE,release
else
    mvn clean deploy -P $PROFILE
fi
```

使用方法：
```bash
# 发布到 Nexus SNAPSHOT 仓库
./deploy.sh nexus

# 发布到阿里云效 RELEASE 仓库
./deploy.sh aliyun release

# 发布到 Maven Central RELEASE 仓库
./deploy.sh central release
```

## 注意事项

1. 发布前确保项目编译通过
2. RELEASE 版本需要 GPG 签名
3. 确保 Maven settings.xml 配置正确
4. 版本号管理使用 `${revision}` 属性
5. 发布到 Maven Central 需要预先申请 Sonatype 账号和项目审批