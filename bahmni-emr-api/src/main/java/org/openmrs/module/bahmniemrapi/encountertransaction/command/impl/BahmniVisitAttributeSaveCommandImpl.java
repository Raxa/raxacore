package org.openmrs.module.bahmniemrapi.encountertransaction.command.impl;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPostSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BahmniVisitAttributeSaveCommandImpl implements EncounterDataPostSaveCommand {
    public static final String VISIT_STATUS_ATTRIBUTE_TYPE = "Visit Status";
    public static final String EMERGENCY_VISIT_TYPE = "Emergency";
    public static final String OPD_VISIT_TYPE = "OPD";
    public static final String ADMISSION_ENCOUNTER_TYPE = "ADMISSION";
    public static final String IPD_VISIT_TYPE = "IPD";
    private VisitService visitService;

    @Autowired
    public BahmniVisitAttributeSaveCommandImpl(VisitService visitService) {
        this.visitService = visitService;
    }

    @Override
    public EncounterTransaction save(BahmniEncounterTransaction bahmniEncounterTransaction, Encounter currentEncounter, EncounterTransaction updatedEncounterTransaction) {
        save(currentEncounter);
        return updatedEncounterTransaction;
    }

    public void save(Encounter currentEncounter) {
        Visit updatedVisit = createOrUpdateVisitAttribute(currentEncounter);
        currentEncounter.setVisit(updatedVisit);
    }

    private Visit createOrUpdateVisitAttribute(Encounter currentEncounter) {
        Visit visit = currentEncounter.getVisit();
        VisitAttribute visitStatus = findVisitAttribute(visit, VISIT_STATUS_ATTRIBUTE_TYPE);

        if (visitStatus == null) {
            String value;
            if (visit.getVisitType().getName().equalsIgnoreCase(EMERGENCY_VISIT_TYPE)) {
                value = visit.getVisitType().getName();
            } else {
                value = OPD_VISIT_TYPE;
            }
            visitStatus = createVisitAttribute(visit, value, VISIT_STATUS_ATTRIBUTE_TYPE);
        }
        if (currentEncounter.getEncounterType().getName().equalsIgnoreCase(ADMISSION_ENCOUNTER_TYPE)) {
            visitStatus.setValueReferenceInternal(IPD_VISIT_TYPE);
        }
        visit.setAttribute(visitStatus);
        return visitService.saveVisit(visit);
    }

    private VisitAttribute createVisitAttribute(Visit visit, String value, String visitAttributeTypeName) {
        VisitAttribute visitStatus = new VisitAttribute();
        visitStatus.setVisit(visit);
        visitStatus.setAttributeType(getVisitAttributeType(visitAttributeTypeName));
        visitStatus.setValueReferenceInternal(value);
        return visitStatus;
    }

    private VisitAttributeType getVisitAttributeType(String visitAttributeTypeName) {
        for (VisitAttributeType visitAttributeType : visitService.getAllVisitAttributeTypes()) {
            if (visitAttributeType.getName().equalsIgnoreCase(visitAttributeTypeName)) {
                return visitAttributeType;
            }
        }
        return null;
    }

    private VisitAttribute findVisitAttribute(Visit visit, String visitAttributeTypeName) {
        for (VisitAttribute visitAttribute : visit.getAttributes()) {
            if (visitAttribute.getAttributeType().getName().equalsIgnoreCase(visitAttributeTypeName)) {
                return visitAttribute;
            }
        }
        return null;
    }
}
