#!/bin/bash -x -e

PATH_OF_CURRENT_SCRIPT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#All config is here
MODULE_DEPLOYMENT_FOLDER=/root/.OpenMRS/modules
CWD=$1
VERSION=$2
WEB_CONTAINER=$3
PROJECT_BASE=$PATH_OF_CURRENT_SCRIPT/../../..

docker cp $PROJECT_BASE/bahmnicore-omod/target/bahmnicore*-$VERSION.omod $WEB_CONTAINER:$MODULE_DEPLOYMENT_FOLDER/bahmnicore-$VERSION.omod
docker cp $PROJECT_BASE/openmrs-elis-atomfeed-client-omod/target/openelis-atomfeed-client*-$VERSION.omod $WEB_CONTAINER:$MODULE_DEPLOYMENT_FOLDER/openelis-atomfeed-client-$VERSION.omod
docker cp $PROJECT_BASE/reference-data/omod/target/reference-data*-$VERSION.omod $WEB_CONTAINER:$MODULE_DEPLOYMENT_FOLDER/reference-data-$VERSION.omod

