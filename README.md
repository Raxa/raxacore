# OpenMRS module bahmnicore

This module provides necessary services for running Bahmni

## Build

Clone the repository and build the omod
   
    git clone https://github.com/bahmni/bahmni-core
    cd bahmni-core
    mvn clean install
    
## Deploy

Copy ```bahmni-core/bahmnicore-omod/target/bahmnicore-omod-VERSION-SNAPSHOT.omod``` into OpenMRS modules directory and restart OpenMRS