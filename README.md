[![Build Status](https://travis-ci.org/Bhamni/bahmni-core.svg?branch=master)](https://travis-ci.org/Bhamni/bahmni-core)

*Now you can deploy your omods to your vagrant box by*
* `mvn clean install` to generate the artifacts
* `./scripts/vagrant-deploy.sh` to deploy the generated omods to your vagrant installation
* `./scripts/vagrant-database.sh` to run liquibase migrations in vagrant