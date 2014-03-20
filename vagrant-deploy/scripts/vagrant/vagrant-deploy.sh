#!/bin/sh -x

PATH_OF_CURRENT_SCRIPT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $PATH_OF_CURRENT_SCRIPT/vagrant_functions.sh

#All config is here
MODULE_DEPLOYMENT_FOLDER=/tmp/deploy_bahmni_core
CWD=$1
SCRIPTS_DIR=$CWD/scripts/vagrant
PROJECT_BASE=$PATH_OF_CURRENT_SCRIPT/../../..

# Setup environment
run_in_vagrant -f "$SCRIPTS_DIR/setup_environment.sh"

# Kill tomcat
run_in_vagrant -f "$SCRIPTS_DIR/tomcat_stop.sh"

# Deploy Bhamni core
scp_to_vagrant $PROJECT_BASE/bahmnicore-omod/target/bahmnicore*.omod $MODULE_DEPLOYMENT_FOLDER

# Copy omod files to the vagrant box - in /tmp
scp_to_vagrant $PROJECT_BASE/openerp-atomfeed-client-omod/target/openerp-atomfeed-client*.omod $MODULE_DEPLOYMENT_FOLDER
scp_to_vagrant $PROJECT_BASE/openmrs-elis-atomfeed-client-omod/target/elisatomfeedclient*.omod $MODULE_DEPLOYMENT_FOLDER

#Deploy them from Vagrant /tmp to appropriate location
run_in_vagrant -f "$SCRIPTS_DIR/deploy_omods.sh"

# Restart tomcat
run_in_vagrant -f "$SCRIPTS_DIR/tomcat_start.sh"