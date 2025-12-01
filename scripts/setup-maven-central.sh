#!/bin/bash

# Maven Central 发布环境设置脚本
# 使用方法: ./scripts/setup-maven-central.sh

set -e

echo "🚀 开始设置 Maven Central 发布环境..."

# 检查必要工具
check_tools() {
    echo "📋 检查必要工具..."
    
    if ! command -v gpg &> /dev/null; then
        echo "❌ GPG 未安装，请先安装 GPG"
        echo "   Ubuntu/Debian: sudo apt-get install gnupg"
        echo "   macOS: brew install gnupg"
        echo "   Windows: 下载 Gpg4win"
        exit 1
    fi
    
    if ! command -v git &> /dev/null; then
        echo "❌ Git 未安装，请先安装 Git"
        exit 1
    fi
    
    echo "✅ 工具检查完成"
}

# 生成 GPG 密钥
generate_gpg_key() {
    echo "🔑 生成 GPG 密钥..."
    
    if [ -z "$SKIP_GPG" ]; then
        echo "请输入 GPG 密钥信息："
        echo "真实姓名: "
        read -r REAL_NAME
        echo "邮箱地址: "
        read -r EMAIL
        echo "备注 (可选): "
        read -r COMMENT
        
        # 生成密钥
        gpg --batch --gen-key <<EOF
Key-Type: RSA
Key-Length: 4096
Subkey-Type: RSA
Subkey-Length: 4096
Name-Real: $REAL_NAME
Name-Email: $EMAIL
Expire-Date: 2y
%commit
EOF
        
        echo "✅ GPG 密钥生成完成"
    else
        echo "⏭️  跳过 GPG 密钥生成"
    fi
}

# 获取密钥 ID
get_key_id() {
    echo "🔍 获取 GPG 密钥 ID..."
    KEY_ID=$(gpg --list-secret-keys --keyid-format LONG | grep sec | head -1 | awk '{print $2}' | cut -d'/' -f2)
    
    if [ -z "$KEY_ID" ]; then
        echo "❌ 未找到 GPG 密钥"
        exit 1
    fi
    
    echo "✅ 找到密钥 ID: $KEY_ID"
    export GPG_KEY_ID=$KEY_ID
}

# 上传公钥到服务器
upload_public_key() {
    echo "📤 上传公钥到密钥服务器..."
    
    if [ -z "$SKIP_UPLOAD" ]; then
        gpg --keyserver hkp://pool.sks-keyservers.net --send-keys "$GPG_KEY_ID"
        echo "✅ 公钥上传完成"
    else
        echo "⏭️  跳过公钥上传"
    fi
}

# 导出私钥
export_private_key() {
    echo "💾 导出私钥..."
    
    gpg --armor --export-secret-keys "$GPG_KEY_ID" > private.key
    echo "✅ 私钥已导出到 private.key"
    echo "⚠️  请妥善保管此文件，并将内容添加到 GitHub Secrets 的 GPG_PRIVATE_KEY"
}

# 生成配置说明
generate_config_guide() {
    echo "📝 生成配置说明..."
    
    cat > GITHUB_SECRETS_GUIDE.md <<EOF
# GitHub Secrets 配置指南

请在 GitHub 仓库设置中添加以下 Secrets：

## 1. Sonatype 账号信息
- **SONATYPE_USERNAME**: 您的 Sonatype JIRA 用户名
- **SONATYPE_PASSWORD**: 您的 Sonatype JIRA 密码

## 2. GPG 密钥信息
- **GPG_PRIVATE_KEY**: 以下私钥内容（包含 BEGIN 和 END 行）：
\`\`\`
$(cat private.key)
\`\`\`

- **GPG_PASSPHRASE**: 您的 GPG 密钥密码（如果设置了的话）

## 3. 验证配置
配置完成后，可以手动触发 GitHub Actions 来测试发布流程。

## 4. Sonatype 申请
如果还没有申请 Maven Central 发布权限，请：
1. 访问 https://issues.sonatype.org
2. 创建新工单申请 io.arkx.framework 的发布权限
3. 等待审批通过后即可正常发布

EOF
    
    echo "✅ 配置说明已生成到 GITHUB_SECRETS_GUIDE.md"
}

# 主函数
main() {
    echo "🎯 Maven Central 发布环境设置向导"
    echo "=================================="
    
    check_tools
    generate_gpg_key
    get_key_id
    upload_public_key
    export_private_key
    generate_config_guide
    
    echo ""
    echo "🎉 设置完成！"
    echo ""
    echo "📋 下一步操作："
    echo "1. 查看 GITHUB_SECRETS_GUIDE.md 文件"
    echo "2. 在 GitHub 仓库中配置相应的 Secrets"
    echo "3. 申请 Sonatype 发布权限（如果还没有的话）"
    echo "4. 测试发布流程：git tag v0.3.0 && git push origin v0.3.0"
    echo ""
    echo "🔑 您的 GPG 密钥 ID: $GPG_KEY_ID"
    echo "📁 私钥文件: private.key"
    echo ""
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-gpg)
            SKIP_GPG=1
            shift
            ;;
        --skip-upload)
            SKIP_UPLOAD=1
            shift
            ;;
        --help)
            echo "使用方法: $0 [选项]"
            echo "选项:"
            echo "  --skip-gpg      跳过 GPG 密钥生成"
            echo "  --skip-upload   跳过公钥上传"
            echo "  --help          显示帮助信息"
            exit 0
            ;;
        *)
            echo "未知选项: $1"
            echo "使用 --help 查看帮助信息"
            exit 1
            ;;
    esac
done

# 执行主函数
main