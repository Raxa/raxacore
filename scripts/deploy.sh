#!/bin/bash

#All config is here
MODULE_DEPLOYMENT_FOLDER=/tmp/deploy_bahmni_core
MACHINE_IP=192.168.33.10
VERSION=4.0-SNAPSHOT
CWD=./
SCRIPTS_DIR=$CWD/scripts

#Setup environment
ssh vagrant@$MACHINE_IP -m $SCRIPTS_DIR/setup_environment.sh
#Kill tomcat
ssh vagrant@$MACHINE_IP -m $SCRIPTS_DIR/tomcat_stop.sh
#Deploy Bhamni core
scp  $CWD/bahmnicore-omod/target/bahmnicore-omod-$VERSION.omod vagrant@$MACHINE_IP:$MODULE_DEPLOYMENT_FOLDER
#Deploy Open erp atom feed client
scp  $CWD/openerp-atomfeed-client-omod/target/openerp-atomfeed-client-omod-$VERSION.omod vagrant@$MACHINE_IP:$MODULE_DEPLOYMENT_FOLDER
#Deploy Open elis
scp  $CWD/openmrs-elis-atomfeed-client-omod/target/elisatomfeedclient-omod-$VERSION.omod vagrant@$MACHINE_IP:$MODULE_DEPLOYMENT_FOLDER
#Copy omods into module directories
ssh vagrant@$MACHINE_IP -m $SCRIPTS_DIR/deploy_omods.sh
#Restart tomcat
ssh vagrant@$MACHINE_IP -m $SCRIPTS_DIR/tomcat_start.sh
