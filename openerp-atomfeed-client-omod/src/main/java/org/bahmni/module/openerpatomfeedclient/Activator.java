package org.bahmni.module.openerpatomfeedclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

public class Activator extends BaseModuleActivator {

    private Log log = LogFactory.getLog(this.getClass());

    @Override
    public void started() {
        log.info("Started the OpenERP Atom Feed Client module");
    }

    @Override
    public void stopped() {
        log.info("Stopped the OpenERP Atom Feed Client module");
    }
}
