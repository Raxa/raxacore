package org.openmrs.module.bahmniemrapi.laborder.service;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResults;

import java.util.Collection;
import java.util.List;

public interface LabOrderResultsService {
    LabOrderResults getAll(Patient patient, List<Visit> visits);

    List<LabOrderResult> getAllForConcepts(Patient patient, Collection<String> concepts, List<Visit> visits);
}
