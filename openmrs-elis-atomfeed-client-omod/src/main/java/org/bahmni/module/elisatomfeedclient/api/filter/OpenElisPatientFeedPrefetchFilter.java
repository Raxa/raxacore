package org.bahmni.module.elisatomfeedclient.api.filter;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.openmrs.util.OpenmrsUtil;

import java.io.IOException;

public class OpenElisPatientFeedPrefetchFilter{
    private static Logger logger = Logger.getLogger(OpenElisPatientFeedPrefetchFilter.class);

    private Interpreter interpreter;

    public OpenElisPatientFeedPrefetchFilter() {
        this(new Interpreter());
    }

    public OpenElisPatientFeedPrefetchFilter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Boolean allows(Event event) {
        String filterScript = null;
        try {
            interpreter.set("event", event);
            filterScript = getApplicationDataDirectory() + "beanshell/openelis-prefetch-eventfilter.bsh";
            return (Boolean) interpreter.source(filterScript);
        } catch (IOException | EvalError error) {
            logger.info("Filter script " + filterScript + " not found. Continuing to process");
            return true;
        }
    }

    protected String getApplicationDataDirectory() {
        return OpenmrsUtil.getApplicationDataDirectory();
    }
}
