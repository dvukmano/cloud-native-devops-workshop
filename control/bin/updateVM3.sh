#!/bin/bash

CONTENT_DIR=/u01/content/cloud-native-devops-workshop

echo "Check proxy settings"

sudo rm -f /etc/profile.d/setproxy.sh

GIT_SYSTEM_PROXY_CHECK=`git config --get --system http.proxy`
if [ -n "$GIT_SYSTEM_PROXY_CHECK" ]; then
  echo "Reset proxy settings for Oracle network"
  . ${CONTENT_DIR}/control/bin/setOracleProxy.sh
else
  echo "Reset proxy settings for Non-Oracle network"
  . ${CONTENT_DIR}/control/bin/removeOracleProxy.sh
  unset http_proxy
  unset https_proxy
fi

echo "http_proxy=$http_proxy"
echo "https_proxy=$https_proxy"

echo "========================================"
FF_SHORTCUT=~/Desktop/chrome.desktop

if grep -q "Exec=/usr/bin/firefox" $FF_SHORTCUT;
then
    echo "Firefox shortcut ready."    
else
    cat > $FF_SHORTCUT <<EOF
[Desktop Entry]
Comment[en_US]=This is Firefox
Comment=This is Firefox
Exec=/usr/bin/firefox
GenericName[en_US]=Firefox
GenericName=Firefox
Icon=firefox
MimeType=
Name[en_US]=Firefox
Name=Firefox
Path=/tmp/
StartupNotify=true
Terminal=false
TerminalOptions=
Type=Application
EOF
    echo "Firefox shortcut has been created."
fi
echo "========================================"

echo "Clean and disable yum cache/PackageKit..."

rm -f /var/run/yum.pid

sudo ps -ef | grep PackageKit | grep -v grep | awk '{print $2}' | xargs -r kill -9 &

sudo yum --enablerepo=* clean all

sudo systemctl disable packagekit

echo "Everything is up to date!"
