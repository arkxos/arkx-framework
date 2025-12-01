@echo off
REM 国内 Maven 仓库快速设置脚本
REM 使用方法: setup-china-repo.bat [repo-type]
REM repo-type: aliyun, tencent, huawei, all

set REPO_TYPE=%1
if "%REPO_TYPE%"=="" set REPO_TYPE=aliyun

echo 🇨🇳 国内 Maven 仓库设置向导
echo ========================
echo.

REM 检查 Maven 安装
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Maven 未安装，请先安装 Apache Maven
    echo    下载地址: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo ✅ Maven 工具检查完成
echo.

REM 创建 Maven 配置目录
if not exist "%USERPROFILE%\.m2" mkdir "%USERPROFILE%\.m2"

REM 备份现有 settings.xml
if exist "%USERPROFILE%\.m2\settings.xml" (
    echo 📦 备份现有 settings.xml...
    copy "%USERPROFILE%\.m2\settings.xml" "%USERPROFILE%\.m2\settings.xml.backup" >nul
    echo ✅ 备份完成
)

REM 根据选择生成配置
echo 🔧 生成 Maven 配置...

if "%REPO_TYPE%"=="aliyun" goto :setup_aliyun
if "%REPO_TYPE%"=="tencent" goto :setup_tencent
if "%REPO_TYPE%"=="huawei" goto :setup_huawei
if "%REPO_TYPE%"=="all" goto :setup_all

echo ❌ 不支持的仓库类型: %REPO_TYPE%
echo    支持的类型: aliyun, tencent, huawei, all
pause
exit /b 1

:setup_aliyun
echo 📦 配置阿里云效仓库...
(
echo ^<settings^>
echo   ^<mirrors^>
echo     ^<mirror^>
echo       ^<id^>aliyun-central^</id^>
echo       ^<mirrorOf^>central^</mirrorOf^>
echo       ^<name^>Aliyun Central Mirror^</name^>
echo       ^<url^>https://maven.aliyun.com/repository/central^</url^>
echo     ^</mirror^>
echo   ^</mirrors^>
echo   ^<servers^>
echo     ^<server^>
echo       ^<id^>aliyun-cloud-releases^</id^>
echo       ^<username^>YOUR_ALIYUN_USERNAME^</username^>
echo       ^<password^>YOUR_ALIYUN_TOKEN^</password^>
echo     ^</server^>
echo     ^<server^>
echo       ^<id^>aliyun-cloud-snapshots^</id^>
echo       ^<username^>YOUR_ALIYUN_USERNAME^</username^>
echo       ^<password^>YOUR_ALIYUN_TOKEN^</password^>
echo     ^</server^>
echo   ^</servers^>
echo ^</settings^>
) > "%USERPROFILE%\.m2\settings.xml"
goto :setup_complete

:setup_tencent
echo 📦 配置腾讯云仓库...
(
echo ^<settings^>
echo   ^<mirrors^>
echo     ^<mirror^>
echo       ^<id^>tencent-central^</id^>
echo       ^<mirrorOf^>central^</mirrorOf^>
echo       ^<name^>Tencent Cloud Central Mirror^</name^>
echo       ^<url^>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/^</url^>
echo     ^</mirror^>
echo   ^</mirrors^>
echo ^</settings^>
) > "%USERPROFILE%\.m2\settings.xml"
goto :setup_complete

:setup_huawei
echo 📦 配置华为云仓库...
(
echo ^<settings^>
echo   ^<mirrors^>
echo     ^<mirror^>
echo       ^<id^>huawei-central^</id^>
echo       ^<mirrorOf^>central^</mirrorOf^>
echo       ^<name^>Huawei Cloud Central Mirror^</name^>
echo       ^<url^>https://repo.huaweicloud.com/repository/maven/^</url^>
echo     ^</mirror^>
echo   ^</mirrors^>
echo ^</settings^>
) > "%USERPROFILE%\.m2\settings.xml"
goto :setup_complete

:setup_all
echo 📦 配置多仓库备份...
(
echo ^<settings^>
echo   ^<mirrors^>
echo     ^<mirror^>
echo       ^<id^>aliyun-central^</id^>
echo       ^<mirrorOf^>central^</mirrorOf^>
echo       ^<name^>Aliyun Central Mirror^</name^>
echo       ^<url^>https://maven.aliyun.com/repository/central^</url^>
echo     ^</mirror^>
echo   ^</mirrors^>
echo   ^<servers^>
echo     ^<server^>
echo       ^<id^>aliyun-cloud-releases^</id^>
echo       ^<username^>YOUR_ALIYUN_USERNAME^</username^>
echo       ^<password^>YOUR_ALIYUN_TOKEN^</password^>
echo     ^</server^>
echo     ^<server^>
echo       ^<id^>aliyun-cloud-snapshots^</id^>
echo       ^<username^>YOUR_ALIYUN_USERNAME^</username^>
echo       ^<password^>YOUR_ALIYUN_TOKEN^</password^>
echo     ^</server^>
echo   ^</servers^>
echo ^</settings^>
) > "%USERPROFILE%\.m2\settings.xml"
goto :setup_complete

:setup_complete
echo ✅ 配置生成完成
echo.

REM 生成项目配置片段
echo 📝 生成项目配置片段...

(
echo ^<!-- 国内 Maven 仓库配置 - %REPO_TYPE% --^>
echo ^<repositories^>
) > pom-repositories.xml

if "%REPO_TYPE%"=="aliyun" goto :add_aliyun_repos
if "%REPO_TYPE%"=="tencent" goto :add_tencent_repos
if "%REPO_TYPE%"=="huawei" goto :add_huawei_repos
if "%REPO_TYPE%"=="all" goto :add_all_repos

:add_aliyun_repos
(
echo   ^<!-- 阿里云效正式版本 --^>
echo   ^<repository^>
echo     ^<id^>aliyun-cloud-releases^</id^>
echo     ^<url^>https://packages.aliyun.com/maven/repository/126334-release-hl3JHL^</url^>
echo     ^<releases^>^<enabled^>true^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>false^</enabled^>^</snapshots^>
echo   ^</repository^>
echo   ^<!-- 阿里云效快照版本 --^>
echo   ^<repository^>
echo     ^<id^>aliyun-cloud-snapshots^</id^>
echo     ^<url^>https://packages.aliyun.com/maven/repository/126334-snapshot-k0fTE8^</url^>
echo     ^<releases^>^<enabled^>false^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>true^</enabled^>^</snapshots^>
echo   ^</repository^>
) >> pom-repositories.xml
goto :finish_repos

:add_tencent_repos
(
echo   ^<!-- 腾讯云仓库 --^>
echo   ^<repository^>
echo     ^<id^>tencent-maven^</id^>
echo     ^<url^>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/^</url^>
echo     ^<releases^>^<enabled^>true^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>true^</enabled^>^</snapshots^>
echo   ^</repository^>
) >> pom-repositories.xml
goto :finish_repos

:add_huawei_repos
(
echo   ^<!-- 华为云仓库 --^>
echo   ^<repository^>
echo     ^<id^>huawei-maven^</id^>
echo     ^<url^>https://repo.huaweicloud.com/repository/maven/^</url^>
echo     ^<releases^>^<enabled^>true^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>true^</enabled^>^</snapshots^>
echo   ^</repository^>
) >> pom-repositories.xml
goto :finish_repos

:add_all_repos
(
echo   ^<!-- 阿里云效仓库 --^>
echo   ^<repository^>
echo     ^<id^>aliyun-cloud-releases^</id^>
echo     ^<url^>https://packages.aliyun.com/maven/repository/126334-release-hl3JHL^</url^>
echo     ^<releases^>^<enabled^>true^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>false^</enabled^>^</snapshots^>
echo   ^</repository^>
echo   ^<repository^>
echo     ^<id^>aliyun-cloud-snapshots^</id^>
echo     ^<url^>https://packages.aliyun.com/maven/repository/126334-snapshot-k0fTE8^</url^>
echo     ^<releases^>^<enabled^>false^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>true^</enabled^>^</snapshots^>
echo   ^</repository^>
echo   ^<!-- 腾讯云仓库 --^>
echo   ^<repository^>
echo     ^<id^>tencent-maven^</id^>
echo     ^<url^>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/^</url^>
echo     ^<releases^>^<enabled^>true^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>true^</enabled^>^</snapshots^>
echo   ^</repository^>
echo   ^<!-- 华为云仓库 --^>
echo   ^<repository^>
echo     ^<id^>huawei-maven^</id^>
echo     ^<url^>https://repo.huaweicloud.com/repository/maven/^</url^>
echo     ^<releases^>^<enabled^>true^</enabled^>^</releases^>
echo     ^<snapshots^>^<enabled^>true^</enabled^>^</snapshots^>
echo   ^</repository^>
) >> pom-repositories.xml
goto :finish_repos

:finish_repos
(
echo ^</repositories^>
) >> pom-repositories.xml

echo ✅ 项目配置片段已生成到 pom-repositories.xml
echo.

REM 测试配置
echo 🧪 测试 Maven 配置...
mvn help:effective-settings >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Maven 配置测试通过
) else (
    echo ⚠️  Maven 配置可能有问题，请检查 settings.xml
)

echo.
echo 🎉 国内仓库设置完成！
echo.
echo 📋 下一步操作：
echo 1. 如果使用阿里云效，请获取 Token 并更新 settings.xml
echo 2. 将 pom-repositories.xml 中的配置复制到您的项目 pom.xml
echo 3. 测试依赖下载：mvn dependency:resolve
echo.
echo 📖 详细文档：CHINA-REPOSITORIES.md
echo.
pause