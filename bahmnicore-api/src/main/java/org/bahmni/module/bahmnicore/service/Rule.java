package org.bahmni.module.bahmnicore.service;

public interface Rule {

    Double getDose(String patientUuid, Double baseDose) throws Exception;
}
