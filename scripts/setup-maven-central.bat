@echo off
REM Maven Central 发布环境设置脚本 (Windows)
REM 使用方法: setup-maven-central.bat

echo 🚀 开始设置 Maven Central 发布环境...
echo.

REM 检查 GPG
where gpg >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ GPG 未安装，请先安装 GPG
    echo    下载地址: https://www.gpg4win.org/
    pause
    exit /b 1
)

echo ✅ GPG 工具检查完成
echo.

REM 生成 GPG 密钥
if not defined SKIP_GPG (
    echo 🔑 生成 GPG 密钥...
    echo 请按照提示输入密钥信息
    echo.
    gpg --full-generate-key
    echo ✅ GPG 密钥生成完成
) else (
    echo ⏭️  跳过 GPG 密钥生成
)
echo.

REM 获取密钥 ID
echo 🔍 获取 GPG 密钥 ID...
for /f "tokens=2 delims=/" %%i in ('gpg --list-secret-keys --keyid-format LONG ^| findstr sec') do set GPG_KEY_ID=%%i
for /f "tokens=1" %%i in ("%GPG_KEY_ID%") do set GPG_KEY_ID=%%i

if defined GPG_KEY_ID (
    echo ✅ 找到密钥 ID: %GPG_KEY_ID%
) else (
    echo ❌ 未找到 GPG 密钥
    pause
    exit /b 1
)
echo.

REM 上传公钥
if not defined SKIP_UPLOAD (
    echo 📤 上传公钥到密钥服务器...
    gpg --keyserver hkp://pool.sks-keyservers.net --send-keys %GPG_KEY_ID%
    echo ✅ 公钥上传完成
) else (
    echo ⏭️  跳过公钥上传
)
echo.

REM 导出私钥
echo 💾 导出私钥...
gpg --armor --export-secret-keys %GPG_KEY_ID% > private.key
echo ✅ 私钥已导出到 private.key
echo ⚠️  请妥善保管此文件，并将内容添加到 GitHub Secrets 的 GPG_PRIVATE_KEY
echo.

REM 生成配置说明
echo 📝 生成配置说明...

echo # GitHub Secrets 配置指南 > GITHUB_SECRETS_GUIDE.md
echo. >> GITHUB_SECRETS_GUIDE.md
echo 请在 GitHub 仓库设置中添加以下 Secrets： >> GITHUB_SECRETS_GUIDE.md
echo. >> GITHUB_SECRETS_GUIDE.md
echo ## 1. Sonatype 账号信息 >> GITHUB_SECRETS_GUIDE.md
echo - **SONATYPE_USERNAME**: 您的 Sonatype JIRA 用户名 >> GITHUB_SECRETS_GUIDE.md
echo - **SONATYPE_PASSWORD**: 您的 Sonatype JIRA 密码 >> GITHUB_SECRETS_GUIDE.md
echo. >> GITHUB_SECRETS_GUIDE.md
echo ## 2. GPG 密钥信息 >> GITHUB_SECRETS_GUIDE.md
echo - **GPG_PRIVATE_KEY**: 以下私钥内容（包含 BEGIN 和 END 行）： >> GITHUB_SECRETS_GUIDE.md
echo \`\`\` >> GITHUB_SECRETS_GUIDE.md
type private.key >> GITHUB_SECRETS_GUIDE.md
echo \`\`\` >> GITHUB_SECRETS_GUIDE.md
echo - **GPG_PASSPHRASE**: 您的 GPG 密钥密码（如果设置了的话） >> GITHUB_SECRETS_GUIDE.md
echo. >> GITHUB_SECRETS_GUIDE.md
echo ## 3. 验证配置 >> GITHUB_SECRETS_GUIDE.md
echo 配置完成后，可以手动触发 GitHub Actions 来测试发布流程。 >> GITHUB_SECRETS_GUIDE.md
echo. >> GITHUB_SECRETS_GUIDE.md
echo ## 4. Sonatype 申请 >> GITHUB_SECRETS_GUIDE.md
echo 如果还没有申请 Maven Central 发布权限，请： >> GITHUB_SECRETS_GUIDE.md
echo 1. 访问 https://issues.sonatype.org >> GITHUB_SECRETS_GUIDE.md
echo 2. 创建新工单申请 io.arkx.framework 的发布权限 >> GITHUB_SECRETS_GUIDE.md
echo 3. 等待审批通过后即可正常发布 >> GITHUB_SECRETS_GUIDE.md

echo ✅ 配置说明已生成到 GITHUB_SECRETS_GUIDE.md
echo.

echo 🎉 设置完成！
echo.
echo 📋 下一步操作：
echo 1. 查看 GITHUB_SECRETS_GUIDE.md 文件
echo 2. 在 GitHub 仓库中配置相应的 Secrets
echo 3. 申请 Sonatype 发布权限（如果还没有的话）
echo 4. 测试发布流程：git tag v0.3.0 && git push origin v0.3.0
echo.
echo 🔑 您的 GPG 密钥 ID: %GPG_KEY_ID%
echo 📁 私钥文件: private.key
echo.
pause