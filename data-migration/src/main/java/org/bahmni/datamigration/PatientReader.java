package org.bahmni.datamigration;

import org.bahmni.datamigration.request.patient.PatientRequest;

import java.io.IOException;

public interface PatientReader {
    PatientRequest nextPatient() throws Exception;
}
