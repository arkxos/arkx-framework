# GitHub 自动发布到 Maven Central 配置指南

## 概述

本项目已配置完整的 GitHub Actions 工作流，支持自动发布到 Maven Central 仓库。

## 🚀 功能特性

- ✅ **多仓库发布**：支持 GitHub Packages、Sonatype、Maven Central
- ✅ **自动 Release 发布**：推送 tag 时自动发布正式版本
- ✅ **自动 Snapshot 发布**：代码提交时自动发布快照版本
- ✅ **手动发布**：支持手动触发发布流程
- ✅ **GPG 签名**：自动进行 GPG 签名
- ✅ **版本管理**：自动版本更新和 GitHub Release 创建
- ✅ **实时可用**：GitHub Packages 和 Sonatype 立即可用，无需等待同步

## 📋 前置配置

### 1. 申请 Sonatype 账号

1. 访问 [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa) 注册账号
2. 创建新工单申请发布权限：
   - **Project**: Community Support - Open Source Project Repository Hosting (OSSRH)
   - **Issue Type**: New Project
   - **Summary**: 申请发布 `io.arkx.framework` 到 Maven Central
   - **Description**: 项目描述和 GitHub 仓库地址
   - **Group Id**: `io.arkx.framework`
   - **Project URL**: `https://github.com/arkxos/arkx-framework`
   - **SCM url**: `scm:git:git://github.com/arkxos/arkx-framework.git`

### 2. 生成 GPG 密钥

```bash
# 生成 GPG 密钥对
gpg --gen-key

# 查看密钥
gpg --list-secret-keys --keyid-format LONG

# 导出公钥到密钥服务器
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID

# 导出私钥（用于 GitHub Actions）
gpg --armor --export-secret-keys YOUR_KEY_ID > private.key
```

### 3. 配置 GitHub Secrets

在 GitHub 仓库设置中添加以下 Secrets：

| Secret 名称 | 描述 | 示例值 |
|------------|------|--------|
| `SONATYPE_USERNAME` | Sonatype 账号用户名 | `your-username` |
| `SONATYPE_PASSWORD` | Sonatype 账号密码 | `your-password` |
| `GPG_PRIVATE_KEY` | GPG 私钥内容 | `-----BEGIN PGP PRIVATE KEY BLOCK-----...` |
| `GPG_PASSPHRASE` | GPG 密钥密码 | `your-gpg-password` |

## 🔧 工作流说明

### 1. 多仓库发布 (`.github/workflows/multi-repo-deploy.yml`)

**触发条件：**
- 推送 tag：`v*` (如 `v0.3.0`) → 发布到 GitHub + Maven Central
- 推送到分支：`main`/`dev` → 发布到 GitHub + Sonatype Snapshots
- 手动触发：可选择目标仓库

**执行流程：**
1. 检测版本类型（Release/Snapshot）
2. 检出代码
3. 设置 JDK 25 环境
4. 更新版本号
5. 构建和测试
6. **并行发布到多个仓库**：
   - GitHub Packages（立即可用）
   - Sonatype Snapshots（仅快照版本）
   - Maven Central（仅正式版本）
7. 创建 GitHub Release
8. 生成部署报告

### 2. 传统 Maven Central 发布 (`.github/workflows/deploy-maven-central.yml`)

**触发条件：**
- 推送 tag：`v*` (如 `v0.3.0`)
- 手动触发：选择版本和发布类型

**执行流程：**
1. 检出代码
2. 设置 JDK 25 环境
3. 更新版本号
4. 构建和测试
5. 导入 GPG 密钥
6. 部署到 Maven Central
7. 创建 GitHub Release

### 3. Snapshot 发布 (`.github/workflows/snapshot.yml`)

**触发条件：**
- 推送到 `main` 或 `dev` 分支
- 手动触发

**执行流程：**
1. 检出代码
2. 设置 JDK 25 环境
3. 设置 SNAPSHOT 版本
4. 构建和测试
5. 导入 GPG 密钥
6. 部署到 Sonatype Snapshots 仓库

## 📖 使用方法

### 方法 1：Tag 发布（推荐）

```bash
# 创建并推送 tag
git tag v0.3.0
git push origin v0.3.0

# GitHub Actions 会自动触发发布流程
```

### 方法 2：手动发布

1. 进入 GitHub 仓库的 Actions 页面
2. 选择 "Deploy to Maven Central" 工作流
3. 点击 "Run workflow"
4. 填写版本号和发布类型
5. 点击 "Run workflow"

### 方法 3：Snapshot 发布

```bash
# 推送到主分支自动触发 Snapshot 发布
git push origin main
# 或
git push origin dev
```

## 📦 版本管理

### 版本号规则
- **Release 版本**：`0.3.0`, `0.3.1`, `1.0.0`
- **Snapshot 版本**：`0.3.1-SNAPSHOT`, `1.0.0-SNAPSHOT`

### 版本更新
- **自动更新**：GitHub Actions 会根据 tag 或输入自动更新 pom.xml 中的版本
- **手动更新**：使用 Maven Versions 插件

```bash
# 更新版本号
mvn versions:set -DnewVersion=0.3.1 -DgenerateBackupPoms=false

# 提交版本更新
git commit -am "Update version to 0.3.1"
git push origin main
```

## 🔍 验证发布

### 1. 检查 GitHub Packages（立即可用）

**URL:** https://github.com/arkxos/arkx-framework/packages

**Maven 配置：**
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/arkxos/arkx-framework</url>
    </repository>
</repositories>
```

### 2. 检查 Sonatype 仓库（立即可用）

**Snapshot 版本：**
- URL: https://oss.sonatype.org/content/repositories/snapshots/
- 搜索：`io.arkx.framework`

**Release 版本：**
- URL: https://oss.sonatype.org/
- 登录后查看 Staging Repositories

### 3. 检查 Maven Central（2-4小时延迟）

**搜索地址：**
- https://search.maven.org/
- https://mvnrepository.com/

**搜索关键词：**
- Group ID: `io.arkx.framework`
- Artifact ID: `arkx-framework`

### 4. 实时可用性对比

| 仓库 | 可用时间 | 推荐用途 |
|------|----------|----------|
| GitHub Packages | ⚡ 立即 | 开发测试、内部使用 |
| Sonatype Snapshots | ⚡ 立即 | 快照版本测试 |
| Maven Central | ⏰ 2-4小时 | 生产环境、正式发布 |

## ⚠️ 注意事项

### 1. 首次发布
- 首次发布需要等待 Sonatype 审批（通常 1-2 个工作日）
- 审批通过后才能正常发布

### 2. GPG 签名
- 确保 GPG 密钥已上传到公钥服务器
- GitHub Actions 中的 GPG 密钥格式要正确

### 3. 版本号
- Release 版本不能以 `-SNAPSHOT` 结尾
- 同一个版本号不能重复发布

### 4. 依赖检查
- 确保所有依赖都是合法的（Apache 2.0 兼容）
- 避免使用有许可证冲突的依赖

## 🛠️ 本地测试

### 本地发布测试

```bash
# 发布到本地仓库
mvn clean install

# 发布到 Sonatype Snapshot（需要配置 settings.xml）
mvn clean deploy -P central-release

# 发布到 Sonatype Staging（需要 GPG 签名）
mvn clean deploy -P central-release,release
```

### settings.xml 配置

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>your-sonatype-username</username>
            <password>your-sonatype-password</password>
        </server>
    </servers>
</settings>
```

## 📞 故障排除

### 常见问题

1. **401 认证失败**
   - 检查 Sonatype 用户名密码
   - 确认 GitHub Secrets 配置正确

2. **GPG 签名失败**
   - 检查 GPG 密钥格式
   - 确认密码正确
   - 检查密钥是否已上传到公钥服务器

3. **版本冲突**
   - 确保版本号唯一
   - 检查是否已存在相同版本

4. **网络超时**
   - 检查网络连接
   - 考虑重试机制

## 📚 参考资料

- [Sonatype OSSRH Guide](https://central.sonatype.org/pages/ossrh-guide.html)
- [Maven Central Repository](https://search.maven.org/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GPG Documentation](https://www.gnupg.org/documentation/)

---

🎉 **恭喜！** 配置完成后，您的项目就可以自动发布到 Maven Central 了！