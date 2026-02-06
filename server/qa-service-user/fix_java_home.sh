#!/bin/bash
echo "正在修复JAVA_HOME..."

# 检测shell类型
if [[ $SHELL == *"zsh"* ]]; then
    CONFIG_FILE=~/.zshrc
elif [[ $SHELL == *"bash"* ]]; then
    CONFIG_FILE=~/.bash_profile
else
    echo "未知的shell类型，请手动设置JAVA_HOME"
    exit 1
fi

# 清除旧的JAVA_HOME设置
sed -i '' '/export JAVA_HOME=/d' $CONFIG_FILE

# 添加新的JAVA_HOME设置
echo 'export JAVA_HOME=$(/usr/libexec/java_home)' >> $CONFIG_FILE
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> $CONFIG_FILE

# 重新加载配置
source $CONFIG_FILE

echo "修复完成！"
echo "JAVA_HOME: $JAVA_HOME"
java -version
