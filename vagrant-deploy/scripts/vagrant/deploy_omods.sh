#!/bin/sh -x

TEMP_LOCATION=/tmp/deploy_bahmni_core
#USER=bahmni
USER=jss
OMOD_LOCATION=/home/$USER/.OpenMRS/modules

sudo rm -f $OMOD_LOCATION/bahmnicore*.omod
sudo rm -f $OMOD_LOCATION/openelis-atomfeed-client*.omod
sudo rm -f $OMOD_LOCATION/openerp-atomfeed-client*.omod
sudo rm -f $OMOD_LOCATION/reference-data*.omod

sudo su - $USER -c "cp -f $TEMP_LOCATION/* $OMOD_LOCATION"
