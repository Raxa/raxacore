package org.bahmni.module.bahmnicore.dao;

import org.bahmni.module.bahmnicore.model.ResultList;

public interface ActivePatientListDao {

    ResultList getPatientList();

    ResultList getPatientsForAdmission();

    ResultList getAdmittedPatients();

    ResultList getPatientsForDischarge();
}
