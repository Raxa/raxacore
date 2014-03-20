#!/bin/sh -x

TEMP_LOCATION=/tmp/deploy_bahmni_core
OMOD_LOCATION=/home/jss/.OpenMRS/modules

rm -f $OMOD_LOCATION/bahmnicore-omod*.omod
rm -f $OMOD_LOCATION/elisatomfeedclient-omod*.omod
rm -f $OMOD_LOCATION/openerp-atomfeed-client-omod*.omod

sudo su - jss -c "cp -f $TEMP_LOCATION/* $OMOD_LOCATION"
