package org.bahmni.module.elisatomfeedclient.api.worker;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;

import java.util.List;

public class VisitHelper {


    public VisitHelper(VisitService visitService) {
        this.visitService = visitService;
    }

    private VisitService visitService;

    public Visit getLatestVisit(Patient patient) {
        List<Visit> activeVisitsByPatient = visitService.getActiveVisitsByPatient(patient);
        if(activeVisitsByPatient.isEmpty()){
            return null;
        }
        Visit latestVisit = activeVisitsByPatient.get(0);
        for (Visit visit : activeVisitsByPatient) {
            if(visit.getStartDatetime().after(latestVisit.getStartDatetime())){
                latestVisit = visit;
            }
        }
        return latestVisit;
    }
}
