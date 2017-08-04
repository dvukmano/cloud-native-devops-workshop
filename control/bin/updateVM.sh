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

echo "Everything is up to date!"
