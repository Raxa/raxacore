#!/bin/sh -x
MACHINE_IP=192.168.33.10
KEY_FILE=~/.vagrant.d/insecure_private_key
ssh vagrant@$MACHINE_IP -i $KEY_FILE "sudo su - jss -c 'cd /bahmni_temp/ && ./run-modules-liquibase.sh'"
