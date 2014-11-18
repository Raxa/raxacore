package org.openmrs.module.bahmniemrapi.laborder.service;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResults;

import java.util.List;

public interface LabOrderResultsService {
    LabOrderResults getAll(Patient patient, List<Visit> visits);
}
