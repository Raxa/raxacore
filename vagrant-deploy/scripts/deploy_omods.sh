#!/bin/sh

TEMP_LOCATION=/tmp/deploy_bahmni_core
OMOD_LOCATION=/home/jss/.OpenMRS/modules

sudo cp -f $TEMP_LOCATION/* $OMOD_LOCATION
