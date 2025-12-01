# 🔐 Token 安全设置指南

## 📋 GitHub Secrets 配置

### 1. 访问 GitHub Secrets
1. 进入 GitHub 仓库
2. 点击 `Settings` 标签
3. 左侧菜单选择 `Secrets and variables` → `Actions`
4. 点击 `New repository secret` 添加 secrets

### 2. 必需的 Secrets 列表

#### Maven Central 发布
| Secret 名称 | 描述 | 获取方式 |
|-----------|------|----------|
| `SONATYPE_USERNAME` | Sonatype Central 账号 | https://central.sonatype.com/ |
| `SONATYPE_PASSWORD` | Sonatype Central 密码 | 账号设置中生成 |
| `GPG_PRIVATE_KEY` | GPG 私钥内容 | `gpg --export-secret-keys --armor` |
| `GPG_PASSPHRASE` | GPG 私钥密码 | 生成密钥时设置的密码 |

#### 国内仓库发布
| Secret 名称 | 描述 | 获取方式 |
|-----------|------|----------|
| `ALIYUN_USERNAME` | 阿里云效账号 | https://packages.aliyun.com/ |
| `ALIYUN_PASSWORD` | 阿里云效密码 | 账号设置中生成 |
| `TENCENT_USERNAME` | 腾讯云开发者账号 | https://cloud.tencent.com/ |
| `TENCENT_PASSWORD` | 腾讯云开发者密码 | 腾讯云访问管理 |
| `GITEE_USERNAME` | Gitee 账号 | https://gitee.com/ |
| `GITEE_PASSWORD` | Gitee 密码 | Gitee 设置中生成 |

## 🛡️ 安全最佳实践

### 1. Token 生成原则
- ✅ 使用最小权限原则
- ✅ 设置合理的过期时间
- ✅ 定期轮换 token
- ✅ 不要在代码中硬编码
- ✅ 使用强密码

### 2. GPG 密钥生成
```bash
# 生成 GPG 密钥对
gpg --full-generate-key --keyring-mode local

# 导出公钥（用于验证）
gpg --armor --export your-email@example.com

# 导出私钥（用于签名，添加到 GitHub Secrets）
gpg --export-secret-keys --armor your-key-id

# 列出密钥
gpg --list-secret-keys --keyid-format LONG
```

### 3. 各平台 Token 获取方式

#### Sonatype Central
1. 登录 https://central.sonatype.com/
2. 进入 Account Settings
3. 生成 User Token
4. 记录 Username 和 Password

#### 阿里云效
1. 登录 https://packages.aliyun.com/
2. 进入个人设置 → AccessTokens
3. 创建新的 Token
4. 设置权限：`read:packages`, `write:packages`

#### 腾讯云
1. 登录腾讯云控制台
2. 进入访问管理 → API密钥管理
3. 创建子账号并授权
4. 生成 SecretKey

#### Gitee
1. 登录 Gitee
2. 进入设置 → 私人令牌
3. 生成新令牌
4. 权限选择：`packages`

### 4. 环境隔离
```bash
# 开发环境（本地）
export MAVEN_USERNAME=dev-user
export MAVEN_PASSWORD=dev-password

# 生产环境（GitHub Actions）
# 通过 Secrets 注入，不在日志中显示
```

### 5. 权限控制
- 🔒 **只读权限**: 用于依赖下载
- 🔓 **读写权限**: 用于包发布
- 🚫 **管理员权限**: 避免使用

### 6. 监控和审计
- 📊 定期检查 token 使用情况
- 🔍 监控异常发布活动
- 📝 记录 token 创建和轮换历史
- 🚨 设置异常告警

## ⚠️ 安全注意事项

### 1. 绝对禁止
- ❌ 在代码中硬编码密码
- ❌ 将 token 提交到版本控制
- ❌ 在日志中打印敏感信息
- ❌ 使用默认密码
- ❌ 共享个人 token

### 2. 推荐做法
- ✅ 使用 GitHub Secrets
- ✅ 定期轮换 token（建议 3-6 个月）
- ✅ 使用不同环境的隔离配置
- ✅ 启用双因素认证
- ✅ 限制 token 权限范围

### 3. 泄露应急处理
1. 立即撤销泄露的 token
2. 生成新的 token
3. 更新 GitHub Secrets
4. 检查使用日志
5. 通知相关人员

## 🔧 验证配置

### 1. 测试连接
```bash
# 测试 Maven Central 连接
mvn deploy -P central-release -DdryRun=true

# 测试阿里云连接
mvn deploy -P aliyun-cloud -DdryRun=true
```

### 2. 验证 GPG 签名
```bash
# 验证 GPG 配置
gpg --list-secret-keys

# 测试签名
echo "test" | gpg --clearsign
```

## 📞 技术支持

如果在配置过程中遇到问题：
1. 检查 GitHub Secrets 格式是否正确
2. 验证 token 权限设置
3. 查看 GitHub Actions 日志
4. 确认仓库权限配置

---

**🔒 记住：安全第一，定期检查，及时更新！**