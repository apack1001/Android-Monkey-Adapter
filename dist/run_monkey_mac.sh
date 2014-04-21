#!/bin/bash
#
chmod a+x ./adb/mac/adb
# 1. Command Line interface of this tool
DEVICE_ID=$1
UNAME=$2
APK_FILE=$3
PKG_NAME=$4
PKG_VERSION=$5
SIG_DURA=$6
SER_DURA=$6
if [ -d "./logs/" ]; then
    echo "./logs/ exists! remove expired folder"
    rm -r ./logs/
fi
# 2. running command
java -jar monkey-adapter-runner.jar --device-id $DEVICE_ID --user-name $UNAME  --pkg-path $APK_FILE --pkg-name $PKG_NAME --pkg-version $PKG_VERSION --single-duration $SIG_DURA --series-duration $SER_DURA
# 3. analyzing command
java -jar monkey-adapter-analyzer.jar --workspaces ./logs/ --monkey-log-file-name monkey_log.txt --logcat-log-file-name logcat_log.txt --traces-log-file-name traces_log.txt --bugreport-log-file-name bugreport_log.txt --properties-file-name properties.txt --duration $SER_DURA --package-name $PKG_NAME
# 4. open html report
open ./logs/index.html
