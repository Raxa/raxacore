#!/bin/bash

#All config is here
MODULE_DEPLOYMENT_FOLDER=/tmp/deploy_bahmni_core
MACHINE_IP=192.168.33.10
VERSION=$2
CWD=$1
SCRIPTS_DIR=$CWD/scripts
KEY_FILE=~/.vagrant.d/insecure_private_key

# Setup environment
ssh vagrant@$MACHINE_IP -i $KEY_FILE < $SCRIPTS_DIR/setup_environment.sh

# Kill tomcat
ssh vagrant@$MACHINE_IP -i $KEY_FILE < $SCRIPTS_DIR/tomcat_stop.sh

# Deploy Bhamni core
scp  -i $KEY_FILE ./bahmnicore-omod/target/bahmnicore-omod-$VERSION.omod vagrant@$MACHINE_IP:$MODULE_DEPLOYMENT_FOLDER

# Copy omod files to the vagrant box - in /tmp
scp -i $KEY_FILE ./openerp-atomfeed-client-omod/target/openerp-atomfeed-client-omod-$VERSION.omod vagrant@$MACHINE_IP:$MODULE_DEPLOYMENT_FOLDER
scp -i $KEY_FILE ./openmrs-elis-atomfeed-client-omod/target/elisatomfeedclient-omod-$VERSION.omod vagrant@$MACHINE_IP:$MODULE_DEPLOYMENT_FOLDER

#Deploy them from Vagrant /tmp to appropriate location
ssh vagrant@$MACHINE_IP -i $KEY_FILE < $SCRIPTS_DIR/deploy_omods.sh

# Restart tomcat
ssh vagrant@$MACHINE_IP -i $KEY_FILE < $SCRIPTS_DIR/tomcat_start.sh
