package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.form.data.FormDetails;

import java.util.Collection;

public interface BahmniFormDetailsService {
    Collection<FormDetails> getFormDetails(String patientUuid, String formType);
}
