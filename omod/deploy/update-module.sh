#!/bin/bash

set -e

if [ $# -lt 2 ]; then
   echo "Usage: update-module admin-password module-file [open-mrs-url]"
	 echo "Default open-mrs-url is http://localhost:8080/openmrs"
   exit 1
fi

if ! [ -f $2 ]; then
   echo Error: module file $2 does not exist
   exit 1
fi

if [ -z "$3" ]; then 
	OPENMRS_URL='http://localhost:8080/openmrs'
else 
	OPENMRS_URL=$3
fi

curl -i -c /tmp/cookie.txt -d uname=admin -d pw=${1} $OPENMRS_URL/loginServlet > /tmp/login_response.txt
curl -i -b /tmp/cookie.txt -F action=upload -F update=true -F moduleFile=\@$2 $OPENMRS_URL/admin/modules/module.list > /tmp/upload_response.txt

rm -rf /tmp/cookie.txt > /dev/null 2>&1

if grep -q "modules/module.list" "/tmp/upload_response.txt"; then
	rm -rf /tmp/upload_response.txt /tmp/login_response.txt > /dev/null 2>&1
else 
	echo "Failed to update module. Please check /tmp/upload_response.txt and /tmp/login_response.txt more info"
	exit 1
fi

