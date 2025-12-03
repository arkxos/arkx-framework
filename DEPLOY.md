# ArkX Framework 部署指南

## 版本管理策略

### 版本管理策略（支持两种模型）

#### 模型一：完整分支模型
- **main分支**: `x.y.z-SNAPSHOT` (始终带SNAPSHOT，版本号递增)
- **feature分支**: `x.y.z-SNAPSHOT` (继承main，不修改pom.xml)
- **integration分支**: `x.y.z-SNAPSHOT` (继承main，不修改pom.xml)
- **pre-release分支**: `x.y.z-SNAPSHOT` (继承main，不修改pom.xml)
- **release分支**: `x.y.z-SNAPSHOT` → `x.y.z` (去除SNAPSHOT发布)

#### 模型二：简化分支模型（推荐）
- **main分支**: `x.y.z-SNAPSHOT` (始终带SNAPSHOT，版本号递增)
- **dev分支**: `x.y.z-SNAPSHOT` (始终带SNAPSHOT，版本号递增，替代feature/integration/pre-release)
- **release分支**: `x.y.z-SNAPSHOT` → `x.y.z` (去除SNAPSHOT发布)

### 版本管理原则
1. **主线分支维护版本号**: main和dev分支都负责版本号递增和SNAPSHOT管理
2. **其他分支不修改版本**: feature、integration、pre-release分支都不修改pom.xml版本号
3. **发布时去SNAPSHOT**: release分支创建时去掉SNAPSHOT后缀
4. **发布后版本更新**: release发布成功后，主线版本号递增并恢复SNAPSHOT

### 版本演进示例
```
# 完整模型
main: 0.1.0-SNAPSHOT → (发布) → release/0.1.0: 0.1.0 → (发布成功) → main: 0.1.1-SNAPSHOT

# 简化模型
main: 0.1.0-SNAPSHOT → (发布) → release/0.1.0: 0.1.0 → (发布成功) → main: 0.1.1-SNAPSHOT
dev:  0.1.0-SNAPSHOT → (发布) → release/0.1.0: 0.1.0 → (发布成功) → dev:  0.1.1-SNAPSHOT
```

### 分支版本管理详细规则

#### 1. dev 分支 - 开发版本（带SNAPSHOT）
```xml
<!-- pom.xml 中的版本 -->
<version>0.1.1-SNAPSHOT</version>
```

**特点：**
- ✅ **始终包含SNAPSHOT后缀**
- ✅ **作为其他分支的基准版本**
- ✅ **CI自动部署SNAPSHOT到Maven Central**
- ✅ **版本号递增在发布后自动更新**

**版本更新时机：**
```bash
# 当 release/* 分支发布成功后，CI自动更新dev版本
# release/0.1.0 发布成功 → dev版本从 0.1.0-SNAPSHOT 更新到 0.1.1-SNAPSHOT
```

#### 2. feature/* 分支 - 功能开发版本
```xml
<!-- pom.xml 中的版本 -->
<version>0.1.1-SNAPSHOT</version>
```

**特点：**
- ✅ **继承dev版本，不修改pom.xml**
- ✅ **完全避免合并时的版本冲突**
- ✅ **CI自动部署SNAPSHOT到Maven Central**
- ✅ **版本号与dev保持一致**

**版本管理策略：**

### 创建功能分支（推荐方式）
```bash
#!/bin/bash
# scripts/create-feature-branch.sh
FEATURE_NAME=$1

# 创建分支（不修改版本号）
git checkout dev
git pull origin dev
git checkout -b feature/$FEATURE_NAME

echo "Created feature/$FEATURE_NAME with version from dev (no pom.xml changes)"
```

**使用方式：**
```bash
# 创建功能分支（版本与dev一致）
./scripts/create-feature-branch.sh user-auth
# 结果：feature/user-auth 分支，版本 0.1.1-SNAPSHOT（与dev一致）

./scripts/create-feature-branch.sh payment
# 结果：feature/payment 分支，版本 0.1.1-SNAPSHOT（与dev一致）
```

### 直接在dev分支开发（简化方案）
```bash
# 最简化方案：直接在dev分支开发
git checkout dev
git pull origin dev

# 开发功能代码...
git add .
git commit -m "feat: add user authentication module"
git push origin dev  # CI自动部署SNAPSHOT
```

### 合并到dev分支（无版本冲突）
```bash
# 1. 多个feature分支版本完全一致
feature/user-auth: 0.1.1-SNAPSHOT
feature/payment:   0.1.1-SNAPSHOT
feature/order:     0.1.1-SNAPSHOT

# 2. 合并到dev分支都无版本冲突
git checkout dev
git merge feature/user-auth    # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT
git merge feature/payment      # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT
git merge feature/order        # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT
```

**版本冲突解决：**
- ✅ **feature分支不修改pom.xml版本，完全避免合并冲突**
- ✅ **所有feature分支版本号与dev完全一致**
- ✅ **CI自动处理SNAPSHOT部署**
- ✅ **简化版本管理，减少复杂性**

#### 3. integration/* 分支 - 集成测试版本
```xml
<!-- pom.xml 中的版本 -->
<version>0.1.1-SNAPSHOT</version>
```

**特点：**
- ✅ **继承main版本，不修改pom.xml**
- ✅ **CI自动部署SNAPSHOT到Maven Central**
- ✅ **临时分支，测试完成后删除**
- ✅ **用于集成测试验证**

**版本管理策略：**

### 创建集成分支（推荐方式）
```bash
#!/bin/bash
# scripts/create-integration-branch.sh
BRANCH_NAME=$1

# 创建分支（不修改版本号）
git checkout main
git pull origin main
git checkout -b integration/$BRANCH_NAME

echo "Created integration/$BRANCH_NAME with version from main (no pom.xml changes)"
```

**使用方式：**
```bash
# 创建集成分支（版本与main一致）
./scripts/create-integration-branch.sh sprint-23
# 结果：integration/sprint-23 分支，版本 0.1.1-SNAPSHOT（与main一致）

./scripts/create-integration-branch.sh module-auth
# 结果：integration/module-auth 分支，版本 0.1.1-SNAPSHOT（与main一致）
```

### 集成测试流程
```bash
# 1. 创建集成分支
git checkout -b integration/sprint-23 main

# 2. 合并多个feature分支（无版本冲突）
git merge feature/user-auth    # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT
git merge feature/payment      # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT
git merge feature/order        # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT

# 3. 推送后CI自动部署SNAPSHOT
git push origin integration/sprint-23  # CI自动构建并部署SNAPSHOT
```

#### 4. pre-release/* 分支 - 预发布测试版本
```xml
<!-- pom.xml 中的版本 -->
<version>0.1.1-SNAPSHOT</version>
```

**特点：**
- ✅ **继承main版本，不修改pom.xml**
- ✅ **CI自动部署SNAPSHOT到Maven Central**
- ✅ **转为release分支后删除**
- ✅ **用于发布前的最终验证**

**版本管理：**

### 创建预发布分支（推荐方式）
```bash
#!/bin/bash
# scripts/create-pre-release-branch.sh
VERSION=$1

# 创建分支（不修改版本号）
git checkout main
git pull origin main
git checkout -b pre-release/$VERSION

echo "Created pre-release/$VERSION with version from main (no pom.xml changes)"
```

**使用方式：**
```bash
# 创建预发布分支（版本与main一致）
./scripts/create-pre-release-branch.sh 0.1.1
# 结果：pre-release/0.1.1 分支，版本 0.1.1-SNAPSHOT（与main一致）
```

### 预发布验证流程
```bash
# 1. 创建预发布分支
git checkout -b pre-release/0.1.1 main

# 2. 合并已验证的feature分支（无版本冲突）
git merge feature/user-auth    # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT
git merge feature/payment      # ✅ 无冲突：0.1.1-SNAPSHOT → 0.1.1-SNAPSHOT

# 3. 推送后CI自动部署SNAPSHOT
git push origin pre-release/0.1.1  # CI自动构建并部署SNAPSHOT

# 4. 验证通过后转为发布分支
git checkout -b release/0.1.1 pre-release/0.1.1
```

#### 5. release/* 分支 - 准发布版本
```xml
<!-- 推送时的版本 -->
<version>0.1.1-SNAPSHOT</version>

<!-- CI自动处理后的版本 -->
<version>0.1.1</version>
```

**特点：**
- ✅ **推送时包含SNAPSHOT后缀**
- ✅ **CI自动去除SNAPSHOT后缀**
- ✅ **完整发布流程到Maven Central**
- ✅ **发布完成后删除**

**CI自动处理流程：**
```yaml
# 1. 检测到release分支推送
# 2. 自动去除SNAPSHOT后缀：0.1.1-SNAPSHOT -> 0.1.1
# 3. 构建并部署到Maven Central
# 4. 创建Git标签v0.1.1
# 5. 合并到main分支
# 6. 删除release分支
```

## 分支结构（支持两种模型）

### 模型一：完整分支结构
```
main                           # 开发分支：x.y.z-SNAPSHOT，自动部署SNAPSHOT
├── feature/*                   # 功能开发分支：x.y.z-SNAPSHOT，继承main版本，不修改pom.xml
│   ├── feature/user-auth      # 用户认证功能 → 0.1.1-SNAPSHOT（与main一致）
│   ├── feature/payment        # 支付功能 → 0.1.1-SNAPSHOT（与main一致）
│   └── feature/order          # 订单功能 → 0.1.1-SNAPSHOT（与main一致）
├── integration/*               # 集成测试分支：x.y.z-SNAPSHOT，继承main版本，不修改pom.xml
│   ├── integration/sprint-23  # 按冲刺周期命名 → 0.1.1-SNAPSHOT（与main一致）
│   ├── integration/module-auth # 按功能模块命名 → 0.1.1-SNAPSHOT（与main一致）
│   └── integration/v0.1.0-test # 按版本号命名 → 0.1.1-SNAPSHOT（与main一致）
├── pre-release/*              # 预发布分支：x.y.z-SNAPSHOT，继承main版本，不修改pom.xml
│   └── pre-release/0.1.1     # 特定版本的预发布 → 0.1.1-SNAPSHOT（与main一致）
└── release/*                  # 发布分支：x.y.z-SNAPSHOT → x.y.z，完整发布流程
    └── release/0.1.1          # 特定版本的发布 → 0.1.1-SNAPSHOT → 0.1.1
```

### 模型二：简化分支结构（推荐）
```
main                           # 稳定分支：x.y.z-SNAPSHOT，自动部署SNAPSHOT
dev                            # 开发分支：x.y.z-SNAPSHOT，自动部署SNAPSHOT
                                # dev分支直接承担feature、integration、pre-release功能
└── release/*                  # 发布分支：x.y.z-SNAPSHOT → x.y.z，完整发布流程
    └── release/0.1.1          # 特定版本的发布 → 0.1.1-SNAPSHOT → 0.1.1
```

### 模型对比
| 特性 | 完整模型 | 简化模型 |
|------|----------|----------|
| 稳定分支 | main | main |
| 开发分支 | - | dev |
| 功能开发 | feature/* | 直接在dev |
| 集成测试 | integration/* | 直接在dev |
| 预发布 | pre-release/* | 直接在dev |
| 发布分支 | release/* | release/* |
| 复杂度 | 高 | 极低 |
| 适用场景 | 大型团队、严格流程 | 小型团队、快速迭代 |

## 分支生命周期管理

### 永久分支
- **main**：永久存在，只进不出，始终保持稳定版本（两种模型都有）
- **dev**：永久存在，只进不出，始终保持开发版本（简化模型）

### 临时分支（使用后删除）
#### 完整模型
- **integration/***：集成测试完成后删除
- **feature/***：合并到目标分支后删除
- **pre-release/***：转为 release 分支后删除
- **release/***：发布完成后删除

#### 简化模型
- **release/***：发布完成后删除
- **无feature分支**：直接在dev分支开发

## 操作流程

### 模型一：完整分支模型操作流程

#### 阶段一：功能开发（人工操作）

#### 1.1 创建功能分支
```bash
# 推荐方式：使用自动化脚本
./scripts/create-feature-branch.sh user-auth
# 结果：feature/user-auth 分支，版本自动更新为 0.1.1-SNAPSHOT

# 开发功能代码...
# 提交代码
git add .
git commit -m "feat: add user authentication module"
git push origin feature/user-auth
```

### 手动创建方式
```bash
# 手动创建功能分支
git checkout main
git pull origin main

# 获取当前main版本并计算下一个版本
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
NEXT_VERSION=$(echo $CURRENT_VERSION | awk -F. '{print $1"."$2"."$3+1"-SNAPSHOT"}')

# 创建分支并更新版本
git checkout -b feature/user-auth
mvn versions:set -DnewVersion=$NEXT_VERSION
git commit -am "chore: update version to $NEXT_VERSION for feature/user-auth"
git push origin feature/user-auth
```

#### 1.2 功能分支管理
```bash
# 功能开发中，定期同步 main 更新（避免冲突）
git checkout main
git pull origin main
git checkout feature/user-auth
git merge main  # 解决冲突后继续开发
```

#### 阶段二：集成测试（人工+CI）

#### 2.1 创建集成分支（三种命名方式）

**方式一：按冲刺周期**
```bash
# 每个冲刺开始时创建
git checkout -b integration/sprint-23 main
# 手动更新版本：0.1.0 -> 0.1.1-integration-SNAPSHOT
vim pom.xml  # 修改版本号
git commit -m "chore: update version to 0.1.1-integration-SNAPSHOT for sprint-23 integration"

git merge feature/user-auth
git merge feature/payment
git merge feature/order
git push origin integration/sprint-23  # CI自动部署SNAPSHOT
```

**方式二：按功能模块**
```bash
# 针对特定模块的集成测试
git checkout -b integration/module-auth main
# 手动更新版本：0.1.0 -> 0.1.1-integration-SNAPSHOT
vim pom.xml  # 修改版本号
git commit -m "chore: update version to 0.1.1-integration-SNAPSHOT for auth module integration"

git merge feature/user-auth
git merge feature/permission
git push origin integration/module-auth  # CI自动部署SNAPSHOT
```

**方式三：按版本号**
```bash
# 为特定版本做准备
git checkout -b integration/v0.1.0-test main
# 手动更新版本：0.1.0 -> 0.1.0-integration-SNAPSHOT（保持主版本号）
vim pom.xml  # 修改版本号
git commit -m "chore: update version to 0.1.0-integration-SNAPSHOT for v0.1.0 integration"

git merge feature/user-auth
git push origin integration/v0.1.0-test  # CI自动部署SNAPSHOT
```

#### 2.2 集成测试流程
```bash
# 1. 推送后CI自动执行
#    - 构建：mvn clean compile test
#    - 部署：mvn deploy (SNAPSHOT版本)
#    - 通知：构建结果通知

# 2. 人工验证集成测试结果
#    - 下载SNAPSHOT版本测试
#    - 运行集成测试套件
#    - 验证功能完整性

# 3. 测试完成后清理分支
git checkout main
git branch -d integration/sprint-23
git push origin --delete integration/sprint-23
```

#### 阶段三：预发布准备（人工+CI）

#### 3.1 创建预发布分支
```bash
# 从 main 创建预发布分支（基于稳定版本）
git checkout main
git pull origin main
git checkout -b pre-release/0.1.0

# 手动更新版本：0.1.0 -> 0.1.0-pre-SNAPSHOT
vim pom.xml  # 修改版本号
git commit -m "chore: update version to 0.1.0-pre-SNAPSHOT for pre-release"

# 选择性合并已测试的功能
git merge feature/user-auth --no-ff  # 保留合并历史
git merge feature/payment --no-ff

# 或使用 cherry-pick 选择特定提交
git cherry-pick <commit-hash-1>
git cherry-pick <commit-hash-2>

# 推送预发布分支（CI自动部署SNAPSHOT）
git push origin pre-release/0.1.0
```

#### 3.2 预发布验证
```bash
# CI自动执行：
# 1. 构建SNAPSHOT版本并部署到Maven Central
# 2. 运行完整测试套件
# 3. 生成构建报告

# 人工验证：
# 1. 下载预发布SNAPSHOT版本
# 2. 在测试环境部署验证
# 3. 确认功能完整性
# 4. 性能测试和安全扫描
```

#### 阶段四：正式发布（人工触发+CI自动）

#### 4.1 创建发布分支（人工操作）
```bash
# 预发布验证通过后，转为发布分支
git checkout -b release/0.1.0 pre-release/0.1.0
# 注意：此时版本仍是 0.1.0-SNAPSHOT，CI会自动处理
git push origin release/0.1.0

# 删除预发布分支
git push origin --delete pre-release/0.1.0
```

#### 4.2 CI自动执行发布流程
推送 release 分支后，GitHub Actions 自动执行：

**步骤1：版本处理**
```yaml
# 自动去除 -SNAPSHOT 后缀
# 0.1.0-SNAPSHOT -> 0.1.0
```

**步骤2：构建和签名**
```yaml
# mvn clean deploy -P release
# GPG签名所有构件
```

**步骤3：部署到Maven Central**
```yaml
# 上传到Sonatype Central
# 等待同步到Maven Central
```

**步骤4：创建Git标签**
```yaml
# git tag -a v0.1.0 -m "Release version 0.1.0"
# git push origin v0.1.0
```

**步骤5：合并到main**
```yaml
# git checkout main
# git merge release/0.1.0 --no-ff
# git push origin main
```

**步骤6：清理发布分支**
```yaml
# git branch -d release/0.1.0
# git push origin --delete release/0.1.0
```

#### 阶段五：发布后验证（人工操作）

### 模型二：简化分支模型操作流程

#### 阶段一：功能开发（直接在dev分支）
```bash
# 1. 切换到dev分支
git checkout dev
git pull origin dev

# 2. 直接开发功能
# 开发用户认证功能...
git add .
git commit -m "feat: add user authentication module"
git push origin dev  # CI自动部署SNAPSHOT

# 3. 继续开发其他功能
# 开发支付功能...
git add .
git commit -m "feat: add payment module"
git push origin dev  # CI自动部署SNAPSHOT

# dev分支承担了feature、integration、pre-release的所有功能
```

#### 阶段二：集成测试（直接在dev分支）
```bash
# 1. dev分支本身就是集成分支
git checkout dev
git pull origin dev

# 2. 运行集成测试
mvn clean test

# 3. 推送后CI自动部署SNAPSHOT到Maven Central
git push origin dev  # CI自动构建并部署
```

#### 阶段三：预发布验证（直接在dev分支）
```bash
# 1. dev分支作为预发布环境
git checkout dev
git pull origin dev

# 2. 确认所有功能已开发完成并测试通过
# 3. 运行完整测试套件
mvn clean verify

# 4. 推送后CI自动部署SNAPSHOT
git push origin dev  # CI自动构建并部署
```

#### 阶段四：正式发布（从dev创建release分支）
```bash
# 1. 从dev创建release分支
git checkout dev
git pull origin dev
git checkout -b release/0.1.1

# 2. 推送release分支，触发CI自动发布
git push origin release/0.1.1

# CI自动执行：
# - 去除SNAPSHOT后缀：0.1.1-SNAPSHOT → 0.1.1
# - 构建并发布到Maven Central
# - 创建Git标签v0.1.1
# - 更新dev版本到0.1.2-SNAPSHOT
# - 删除release分支
```

#### 阶段五：发布后验证（人工操作）
```bash
# 1. 验证Maven Central同步
curl https://search.maven.org/artifact/io.arkx.framework/arkx-framework/0.1.1/pom

# 2. 验证Git标签创建
git tag -l | grep v0.1.1

# 3. 验证dev分支版本更新
git checkout dev
git pull origin dev
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
# 应该显示：0.1.2-SNAPSHOT

# 4. 更新文档和CHANGELOG
vim CHANGELOG.md
git add CHANGELOG.md
git commit -m "docs: update CHANGELOG for v0.1.1"
git push origin dev
```

#### 5.1 发布验证
```bash
# 1. 验证Maven Central同步
#    curl https://search.maven.org/artifact/io.arkx.framework/arkx-framework/0.1.0/pom

# 2. 验证Git标签创建
#    git tag -l | grep v0.1.0

# 3. 验证main分支更新
#    git checkout main
#    git log --oneline -5

# 4. 更新文档和CHANGELOG
#    vim CHANGELOG.md
#    git add CHANGELOG.md
#    git commit -m "docs: update CHANGELOG for v0.1.0"
#    git push origin main
```

## 人工操作 vs CI自动化

### 人工操作
- ✅ 创建所有分支（feature, integration, pre-release, release）
- ✅ 合并代码和解决冲突（版本冲突已消除）
- ✅ 功能开发和代码审查
- ✅ 集成测试验证
- ✅ 预发布验证
- ✅ 发布后验证和文档更新
- ✅ 分支清理（integration, feature, pre-release）
- ✅ **版本号管理**：只有release发布后更新main版本号

### CI自动化
- 🤖 **构建：`mvn clean compile test`**（所有分支）
- 🤖 **部署SNAPSHOT：`mvn deploy`**（main, feature, integration, pre-release分支）
- 🤖 **版本处理：去除`-SNAPSHOT`后缀**（release分支）
- 🤖 **GPG签名和构建**（release分支）
- 🤖 **部署到Maven Central**（release分支）
- 🤖 **创建Git标签**（release分支）
- 🤖 **合并到main并更新版本**（release分支：发布成功后main版本+1+SNAPSHOT）
- 🤖 **清理release分支**

## GitHub Actions 自动化工作流

### 1. 简化版本管理工作流（已优化）

**触发条件：**
- 推送到任何分支（main, feature, integration, pre-release）

**自动执行：**
1. 检测分支类型
2. feature/integration/pre-release分支：直接部署SNAPSHOT（不修改版本）
3. release分支：去除SNAPSHOT后缀并发布到Maven Central
4. 发布成功后：自动更新main版本号（版本+1+SNAPSHOT）

**版本管理规则：**
```bash
main:                 0.1.1-SNAPSHOT → (发布release/0.1.1成功后) → 0.1.2-SNAPSHOT
feature/user-auth:    0.1.1-SNAPSHOT (继承main版本，不修改pom.xml)
feature/payment:      0.1.1-SNAPSHOT (继承main版本，不修改pom.xml)
integration/sprint-23: 0.1.1-SNAPSHOT (继承main版本，不修改pom.xml)
pre-release/0.1.1:    0.1.1-SNAPSHOT (继承main版本，不修改pom.xml)
release/0.1.1:        0.1.1-SNAPSHOT → 0.1.1 (去除SNAPSHOT发布)
```

### 2. 手动创建分支工作流（create-branch.yml）

**使用方式：**
1. 进入GitHub仓库的Actions页面
2. 选择"Create Branch with Version"工作流
3. 点击"Run workflow"
4. 填写参数：
   - **Branch type**: feature/integration/pre-release
   - **Branch name**: 分支名称（不含前缀）
   - **Target version**: 可选，不填则自动计算

**示例：**
```yaml
Branch type: feature
Branch name: user-auth
Target version: (留空，自动计算为0.1.1-SNAPSHOT)
```

### 3. 环境变量配置

在GitHub仓库设置中添加以下Secrets：
```bash
MAVEN_USERNAME      # Maven Central用户名
MAVEN_PASSWORD      # Maven Central密码
GPG_PRIVATE_KEY     # GPG私钥
GPG_PASSPHRASE      # GPG密码
```

## 使用示例

### 方式一：直接创建分支（推荐）
```bash
# 本地创建分支，无版本更新步骤
git checkout main
git pull origin main
git checkout -b feature/user-auth
git push origin feature/user-auth  # CI自动部署SNAPSHOT，无版本冲突
```

### 方式二：手动触发（可选）
```bash
# 通过GitHub Actions界面手动创建分支（如果需要）
1. 访问 GitHub Actions 页面
2. 选择 "Create Branch" 工作流
3. 填写分支名称并运行
```

### 方式三：本地脚本（可选）
```bash
# 使用本地脚本创建分支
./scripts/create-feature-branch.sh user-auth
# 脚本只负责创建分支，不修改版本号
```

## 复杂场景处理

### 功能依赖关系处理
```bash
# 场景：feature/payment 依赖 feature/user-auth
# 1. 先合并依赖的功能
git checkout integration/sprint-23
git merge feature/user-auth
git push origin integration/sprint-23  # 测试user-auth

# 2. 再合并依赖它的功能
git merge feature/payment
git push origin integration/sprint-23  # 测试payment+user-auth
```

### 紧急修复流程
```bash
# 1. 从main创建hotfix分支
git checkout -b hotfix/critical-security main

# 2. 修复问题
vim SecurityFix.java
git commit -m "fix: critical security vulnerability"
git push origin hotfix/critical-security

# 3. 创建预发布分支（跳过集成测试）
git checkout -b pre-release/0.1.1 main
# 使用Maven插件自动更新版本
mvn versions:set -DnewVersion=0.1.1-pre-SNAPSHOT
git commit -am "chore: update version to 0.1.1-pre-SNAPSHOT for hotfix"
git merge hotfix/critical-security
git push origin pre-release/0.1.1

# 4. 快速发布
git checkout -b release/0.1.1 pre-release/0.1.1
git push origin release/0.1.1  # CI自动发布
```

### 多版本并行开发
```bash
# 维护多个版本时（版本号管理极大简化）
main                    # 最新开发版本 0.2.1-SNAPSHOT
├── feature/user-auth   # 用户认证功能 → 0.2.1-SNAPSHOT（与main一致）
├── feature/payment     # 支付功能 → 0.2.1-SNAPSHOT（与main一致）
├── pre-release/0.2.0   # 2.0版本预发布 → 0.2.1-SNAPSHOT（与main一致）
└── integration/sprint-24  # 集成测试 → 0.2.1-SNAPSHOT（与main一致）

# 发布流程：
# 1. pre-release/0.2.0 → release/0.2.0 → 发布成功
# 2. CI自动更新main版本：0.2.1-SNAPSHOT → 0.2.2-SNAPSHOT
# 3. 所有分支继续使用新的main版本号
```

## Maven 仓库配置

### SNAPSHOT 版本
```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### RELEASE 版本
```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://central.sonatype.com/repository/maven-releases/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>
```

## 依赖使用

### 在项目中使用
```xml
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.1.1-SNAPSHOT</version> <!-- 开发版本 -->
</dependency>
```

```xml
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-framework</artifactId>
    <version>0.1.0</version> <!-- 稳定版本 -->
</dependency>
```

### BOM 依赖管理
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.arkx.framework</groupId>
            <artifactId>arkx-framework</artifactId>
            <version>${arkx.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<properties>
    <arkx.version>0.1.1-SNAPSHOT</arkx.version>
</properties>
```

## 发布检查清单

发布前请确认：

- [ ] 所有测试通过
- [ ] 文档已更新
- [ ] CHANGELOG.md 已更新
- [ ] 版本号符合语义化版本规范
- [ ] GPG 密钥已配置
- [ ] Sonatype Central 凭据已配置

## 故障排查

### SNAPSHOT 部署失败
1. 检查版本号是否包含 `-SNAPSHOT` 后缀
2. 确认 Sonatype Central 凭据正确
3. 查看构建日志中的错误信息

### RELEASE 部署失败
1. 检查 GPG 签名配置
2. 确认版本号不包含 `-SNAPSHOT`
3. 验证 pom.xml 中的必需元数据

## 相关链接

- [Maven Central 搜索](https://search.maven.org/artifact/io.arkx.framework/arkx-framework)
- [Sonatype Central Portal](https://central.sonatype.com/)
- [GitHub Actions](https://github.com/arkxos/arkx-framework/actions)