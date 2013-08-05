package org.bahmni.module.elisatomfeedclient;

import org.apache.log4j.Logger;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

public class Activator extends BaseModuleActivator {

    private static Logger logger = Logger.getLogger(Activator.class);
	
	@Override
	public void started() {
		logger.info("Started the Open-Elis Atom Feed Client module");
	}
	
	@Override
	public void stopped() {
		logger.info("Stopped the Open-Elis Atom Feed Client module");
	}
}
