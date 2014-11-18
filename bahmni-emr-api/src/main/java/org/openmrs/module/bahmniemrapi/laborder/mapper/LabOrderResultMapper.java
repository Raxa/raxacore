package org.openmrs.module.bahmniemrapi.laborder.mapper;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class LabOrderResultMapper {
    public static final String LAB_RESULT = "LAB_RESULT";
    public static final String LAB_ABNORMAL = "LAB_ABNORMAL";
    public static final String LAB_MINNORMAL = "LAB_MINNORMAL";
    public static final String LAB_MAXNORMAL = "LAB_MAXNORMAL";
    public static final String LAB_NOTES = "LAB_NOTES";
    public static final String LABRESULTS_CONCEPT = "LABRESULTS_CONCEPT";
    private static final String REFERRED_OUT = "REFERRED_OUT";
    public static final String LAB_REPORT = "LAB_REPORT";
    private ConceptService conceptService;

    @Autowired
    public LabOrderResultMapper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public Obs map(LabOrderResult labOrderResult, Order testOrder, Concept concept) {
        try {
            Date obsDate = labOrderResult.getResultDateTime();
            Obs topLevelObs = newObs(testOrder, obsDate, concept, null);
            Obs labObs = newObs(testOrder, obsDate, concept, null);
            topLevelObs.addGroupMember(labObs);
            if(StringUtils.isNotBlank(labOrderResult.getResult())) {
                labObs.addGroupMember(newObs(testOrder, obsDate, concept, labOrderResult.getResult()));
                if(labOrderResult.getAbnormal() != null) {
                    labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_ABNORMAL), labOrderResult.getAbnormal().toString()));
                }
                if (concept.isNumeric() && hasRange(labOrderResult)) {
                    labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_MINNORMAL), labOrderResult.getMinNormal().toString()));
                    labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_MAXNORMAL), labOrderResult.getMaxNormal().toString()));
                }
            }
            if (labOrderResult.getReferredOut() != null && labOrderResult.getReferredOut()) {
                labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(REFERRED_OUT), null));
            }
            if (StringUtils.isNotBlank(labOrderResult.getNotes())) {
                labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_NOTES), labOrderResult.getNotes()));
            }
            if(StringUtils.isNotBlank(labOrderResult.getUploadedFileName())) {
                labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_REPORT), labOrderResult.getUploadedFileName()));
            }
            return topLevelObs;
        } catch (ParseException e) {
            throw new APIException(e);
        }
    }

    private Concept getConceptByName(String conceptName) {
        return conceptService.getConceptByName(conceptName);
    }

    private Obs newObs(Order order, Date obsDate, Concept concept, String value) throws ParseException {
        Obs obs = new Obs();
        obs.setConcept(concept);
        obs.setOrder(order);
        obs.setObsDatetime(obsDate);
        if(StringUtils.isNotBlank(value)) {
            obs.setValueAsString(value);
        }
        return obs;
    }

    private boolean hasRange(LabOrderResult labOrderResult) {
        return labOrderResult.getMinNormal() != null && labOrderResult.getMaxNormal() != null;
    }
}
