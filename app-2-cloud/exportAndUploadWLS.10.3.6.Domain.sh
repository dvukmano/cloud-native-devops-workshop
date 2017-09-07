#!/bin/sh


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]; then
  echo "usage: ${0} <cloud user> <cloud password> <identity domain> [<dbcs service name>]";
  exit -1;
fi

opcuser=${1}
opcpassword=${2}
identitydomain=${3}

# Default DBCS service name to petstoreDB if not specified
if [ -z "$4" ]; then
   dbcservice="petstoreDB";
else
  dbcservice=${4};
fi

echo "********** CREATE 'app2cloud' CLOUD STORAGE CONTAINER TO UPLOAD ARTIFACTS ***********"
tokenline=$(curl -v -X GET \
     -H "X-Storage-User: Storage-$identitydomain:$opcuser" \
     -H "X-Storage-Pass: $opcpassword" \
     https://$identitydomain.storage.oraclecloud.com/auth/v1.0 2>&1 | grep X-Auth-Token)

token=${tokenline##*:}

echo "AUTH Token for storage: " $token

curl -v -X PUT \
     -H "X-Auth-Token: $token" \
     https://$identitydomain.storage.oraclecloud.com/v1/Storage-$identitydomain/app2cloud 2>&1 | grep 'HTTP/1.1\|Accepted'

echo "********** HEALTHCHECK OF THE ON_PREMISE WLS 10.3.6 DOMAIN ***********"

/u01/oracle_jcs_app2cloud/bin/a2c-healthcheck.sh -oh /u01/wins/wls1036 -adminUrl t3://localhost:7001 -adminUser weblogic -outputDir /u01/jcs_a2c_output << EOF
welcome1
EOF

echo "********** EXPORT ON_PREMISE WLS 10.3.6 DOMAIN ***********"

/u01/oracle_jcs_app2cloud/bin/a2c-export.sh -oh /u01/wins/wls1036 -domainDir /u01/wins/wls1036/user_projects/domains/petstore_domain -archiveFile /u01/jcs_a2c_output/petstore_domain.zip -cloudStorageContainer Storage-$identitydomain/app2cloud -cloudStorageUser $opcuser << EOF
$opcpassword
EOF

echo "********** DATABASE CLOUD SERVICE PREPARATION ***********"

dbcs_ip=$(curl -s --request GET --user $opcuser:$opcpassword --header "X-ID-TENANT-NAME:$identitydomain" https://dbaas.oraclecloud.com/paas/service/dbcs/api/v1.1/instances/$identitydomain/$dbcservice | jq -r '.dbaasmonitor_url' | egrep -o '([0-9]{1,3}[.]){3}[0-9]{1,3}')

echo "Database Cloud Service IP address:" $dbcs_ip

chmod 600 ../cloud-utils/tmp.pk

./prepareDBCS.sh system Welcome_1 ../cloud-utils/tmp.pk $dbcs_ip
