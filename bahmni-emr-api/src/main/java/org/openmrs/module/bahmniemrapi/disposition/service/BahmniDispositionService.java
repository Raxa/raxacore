package org.openmrs.module.bahmniemrapi.disposition.service;

import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.disposition.contract.BahmniDisposition;

import java.util.List;

public interface BahmniDispositionService {
    List<BahmniDisposition> getDispositionByVisitUuid(String visitUuid);
    List<BahmniDisposition> getDispositionByVisits(List<Visit> visits);

}
