package org.openmrs.module.bahmniemrapi.laborder.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class LabOrderResultMapper {
    private static final Log log = LogFactory.getLog(LabOrderResultMapper.class);
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
            if (isNotBlank(labOrderResult.getResult()) || isNotBlank(labOrderResult.getUploadedFileName())) {
                labObs.addGroupMember(newResultObs(testOrder, obsDate, concept, labOrderResult));
                if(BooleanUtils.isTrue(labOrderResult.getAbnormal())) {
                    labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_ABNORMAL), labOrderResult.getAbnormal().toString()));
                }
                if (concept.isNumeric() && hasRange(labOrderResult)) {
                    labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_MINNORMAL), labOrderResult.getMinNormal().toString()));
                    labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_MAXNORMAL), labOrderResult.getMaxNormal().toString()));
                }
            }
            if (labOrderResult.getReferredOut() != null && labOrderResult.getReferredOut()) {
                labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(REFERRED_OUT), labOrderResult.getReferredOut().toString()));
            }
            if (isNotBlank(labOrderResult.getNotes())) {
                labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_NOTES), labOrderResult.getNotes()));
            }
            if (isNotBlank(labOrderResult.getUploadedFileName())) {
                labObs.addGroupMember(newObs(testOrder, obsDate, getConceptByName(LAB_REPORT), labOrderResult.getUploadedFileName()));
            }
            return topLevelObs;
        } catch (ParseException e) {
            throw new APIException(e);
        }
    }

    private Obs newResultObs(Order testOrder, Date obsDate, Concept concept, LabOrderResult labOrderResult) throws ParseException {
        Obs obs = new Obs();
        obs.setConcept(concept);
        obs.setOrder(testOrder);
        obs.setObsDatetime(obsDate);
        if (concept.getDatatype().getHl7Abbreviation().equals("CWE")) {
            String resultUuid = labOrderResult.getResultUuid();
            Concept conceptAnswer = isEmpty(resultUuid) ? null : conceptService.getConceptByUuid(resultUuid);
                obs.setValueCoded(conceptAnswer);
            if (conceptAnswer == null) {
                log.warn(String.format("Concept is not available in OpenMRS for ConceptUuid : [%s] , In Accession : [%s]"
                        , resultUuid,labOrderResult.getAccessionUuid()));
                return null;
            }
            return obs;
        }

        if (isEmpty(labOrderResult.getResult())) {
            return null;
        }
        obs.setValueAsString(labOrderResult.getResult());
        return obs;
    }

    private Concept getConceptByName(String conceptName) {
        return conceptService.getConceptByName(conceptName);
    }

    private Obs newObs(Order order, Date obsDate, Concept concept, String value) throws ParseException {
        Obs obs = new Obs();
        obs.setConcept(concept);
        obs.setOrder(order);
        obs.setObsDatetime(obsDate);
        if (isNotBlank(value)) {
            obs.setValueAsString(value);
        }
        return obs;
    }

    private boolean hasRange(LabOrderResult labOrderResult) {
        return labOrderResult.getMinNormal() != null && labOrderResult.getMaxNormal() != null;
    }
}
