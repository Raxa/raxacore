package org.bahmni.module.elisatomfeedclient.api.client.impl;

import bsh.EvalError;
import bsh.Interpreter;
import org.openmrs.util.OpenmrsUtil;

import java.io.*;

public class HealthCenterFilterRule {

    private final Interpreter interpreter;

    public HealthCenterFilterRule() {
        this.interpreter = new Interpreter();
    }

    public Boolean passesWith(String healthCenter) {
        try {
            interpreter.set("healthCenter", healthCenter);
            return (Boolean) interpreter.source(OpenmrsUtil.getApplicationDataDirectory() + "beanshell/open-elis-patient-feed-filter.bsh");
        } catch (IOException | EvalError error) {
            throw new RuntimeException(error);
        }
    }
}
