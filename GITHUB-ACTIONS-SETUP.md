# GitHub Actions 发布权限配置

## 问题描述
在执行发布流程时，GitHub Actions 可能会遇到权限错误：
```
remote: Permission to arkxos/arkx-framework.git denied to github-actions[bot].
fatal: unable to access 'https://github.com/arkxos/arkx-framework/': The requested URL returned error: 403
```

## 解决方案

### 方案 1：配置 Personal Access Token (PAT) - 推荐

1. **创建 Personal Access Token**:
   - 访问 https://github.com/settings/tokens
   - 点击 "Generate new token (classic)"
   - 选择以下权限：
     - `repo` (完整仓库访问权限)
     - `workflow` (更新工作流文件权限)
   - 复制生成的 token

2. **在 GitHub 仓库中配置 Secret**:
   - 进入仓库的 Settings > Secrets and variables > Actions
   - 点击 "New repository secret"
   - 名称: `PAT_TOKEN`
   - 值: 粘贴刚才复制的 token

### 方案 2：使用 GITHUB_TOKEN (有限权限)

如果不想使用 PAT，可以使用默认的 GITHUB_TOKEN，但功能有限：

```yaml
token: ${{ secrets.GITHUB_TOKEN }}
```

限制：
- 无法触发其他工作流
- 在某些操作中权限受限

### 方案 3：手动完成发布步骤

如果配置权限有困难，可以手动完成部分步骤：

1. **自动运行**: Maven 构建、打包、上传到 Maven Central
2. **手动完成**: 推送标签、合并分支

## 当前配置

工作流文件已配置为：
- 优先使用 `PAT_TOKEN`
- 如果没有配置 `PAT_TOKEN`，回退到 `GITHUB_TOKEN`
- 添加了错误处理和详细提示

## 验证配置

运行以下命令检查配置：

```bash
# 检查工作流权限设置
grep -A 5 "permissions:" .github/workflows/release-management.yml

# 检查 token 配置
grep -n "PAT_TOKEN\|GITHUB_TOKEN" .github/workflows/release-management.yml
```

## 故障排除

### 1. Token 权限不足
确保 PAT 具有以下权限：
- ✅ `repo` - 完整仓库访问
- ✅ `workflow` - 工作流权限

### 2. Secret 配置错误
- 检查 Secret 名称是否为 `PAT_TOKEN`
- 确认 token 没有过期
- 确认 token 没有额外的空格或换行符

### 3. 分支保护规则
如果启用了分支保护规则，可能需要额外配置：
- 在 Settings > Branches 中配置规则
- 确保 "Allow administrators to bypass" 已启用

## 相关链接

- [GitHub Personal Access Tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
- [GitHub Actions Permissions](https://docs.github.com/en/actions/security-guides/automatic-token-authentication)
- [Workflow Syntax for GitHub Actions](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)