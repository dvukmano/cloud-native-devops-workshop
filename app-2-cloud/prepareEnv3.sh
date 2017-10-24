#!/bin/sh

APP2CLOUD_DIR=$PWD

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "usage: ${0} <db user> <db password>";
  exit -1;
fi

dbuser=${1}
dbpassword=${2}

#*************************************************************
# Test to see if Oracle database is running
#*************************************************************
check_stat=`ps -ef|grep ${ORACLE_SID}|grep pmon|wc -l`;
oracle_num=`expr ${check_stat}`
if [ $oracle_num -lt 1 ]
then
  echo "Oracle database (sid: ${ORACLE_SID}) is NOT running. Starting database first."
    
sqlplus -s sys/${dbpassword} as sysdba <<EOF
set feedback off;
set serveroutput on;
startup
exit;
EOF

else
  echo "Oracle database (sid: ${ORACLE_SID}) is running."
fi

lsnrctl start

sleep 3

echo "********** CREATING PetStore DB USER **********************************************"

sqlplus -s system/${dbpassword} <<EOF
 drop user petstore cascade;

 create user petstore identified by ${dbpassword};
 grant DBA to petstore;
 exit;
EOF

echo "********** CREATING DB ENTRIES FOR PetStore Application ***************************"

sqlplus petstore/${dbpassword} <<EOF
 @../dpct/sql/petstore.sql
 exit;
EOF

export MW_HOME=/u01/wins/wls1036

echo "********** CREATING PETSTORE_DOMAIN (WEBLOGIC 10.3.6 - PETSTORE_DOMAIN) ***********"
killall java
rm -rf $MW_HOME/user_projects/domains/petstore_domain

$MW_HOME/wlserver_10.3/common/bin/unpack.sh -template=template/petstore_domain_template.jar -domain=$MW_HOME/user_projects/domains/petstore_domain -user_name=weblogic -password=welcome1 -log=petstore_domain_creation.log

export DOMAIN_HOME=/u01/wins/wls1036/user_projects/domains/petstore_domain

sed 's|umask 037|umask 037\n\nJAVA_OPTIONS="-Dweblogic.security.SSL.ignoreHostnameVerification=true -Djava.security.egd=file:/dev/./urandom -Dweblogic.security.allowCryptoJDefaultJCEVerification=true"\n\nUSER_MEM_ARGS="-Xms768m -Xmx768m -XX:MaxPermSize=256m"|g' -i $DOMAIN_HOME/bin/startWebLogic.sh

mkdir -p $DOMAIN_HOME/servers/mserver1/security
mkdir -p $DOMAIN_HOME/servers/mserver2/security

echo -e "username=weblogic\npassword=welcome1" > $DOMAIN_HOME/servers/mserver1/security/boot.properties
echo -e "username=weblogic\npassword=welcome1" > $DOMAIN_HOME/servers/mserver2/security/boot.properties

echo "********** STARTING ADMIN SERVER (WEBLOGIC 10.3.6 - PETSTORE_DOMAIN) **************"
cd $DOMAIN_HOME/bin
nohup ./startWebLogic.sh &>adminserver.log </dev/null  &

sleep 2

tail -f $DOMAIN_HOME/bin/adminserver.log | while read LOGLINE
do
   [[ "${LOGLINE}" == *"Server state changed to RUNNING"* ]] && pkill -P $$ tail
done

echo "********** STARTING MSERVER1 SERVER (WEBLOGIC 10.3.6 - PETSTORE_DOMAIN) ***********"
nohup ./startManagedWebLogic.sh mserver1 localhost:7001 &>mserver1.log </dev/null  &

sleep 2

tail -f $DOMAIN_HOME/bin/mserver1.log | while read LOGLINE
do
   [[ "${LOGLINE}" == *"Server state changed to RUNNING"* ]] && pkill -P $$ tail
done

echo "********** STARTING MSERVER2 SERVER (WEBLOGIC 10.3.6 - PETSTORE_DOMAIN) ***********"
nohup ./startManagedWebLogic.sh mserver2 localhost:7001 &>mserver2.log </dev/null  &

sleep 2

tail -f $DOMAIN_HOME/bin/mserver2.log | while read LOGLINE
do
   [[ "${LOGLINE}" == *"Server state changed to RUNNING"* ]] && pkill -P $$ tail
done

echo "********** ADMIN SERVER (WEBLOGIC 10.3.6 - DOMAIN1036) HAS BEEN STARTED ***********"

cd $APP2CLOUD_DIR

unzip -j -o ../dpct/domain-templates/Domain1036.jar _apps_/petstore.12.war -d $APP2CLOUD_DIR

cp wlst/deployPetstore_template.py wlst/deployPetstore.py
sed "s|@database.dba.pass@|${dbpassword}|g; s|@database.pdb@|XE|g" -i wlst/deployPetstore.py

echo "********** DEPLOY PETSTORE (WEBLOGIC 10.3.6 - PETSTORE_DOMAIN) ********************"

cd $DOMAIN_HOME/bin 
. ./setDomainEnv.sh

cd $APP2CLOUD_DIR

$JAVA_HOME/bin/java -Dweblogic.deploy.UploadLargeFile=true -Dweblogic.RootDirectory=$DOMAIN_HOME weblogic.WLST $APP2CLOUD_DIR/wlst/deployPetstore.py

rm -f $APP2CLOUD_DIR/wlst/deployPetstore.py

echo "********** OPEN PETSTORE APPLICATION AT http://localhost:7003/petstore/faces/catalog.jsp"



