package org.bahmni.module.elisatomfeedclient.api.worker;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;
import org.joda.time.DateTime;
import org.openmrs.*;
import org.openmrs.api.ConceptService;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ResultObsHelper {
    public static final String LAB_RESULT = "LAB_RESULT";
    public static final String LAB_ABNORMAL = "LAB_ABNORMAL";
    public static final String LAB_MINNORMAL = "LAB_MINNORMAL";
    public static final String LAB_MAXNORMAL = "LAB_MAXNORMAL";
    public static final String LAB_NOTES = "LAB_NOTES";
    public static final String LABRESULTS_CONCEPT = "LABRESULTS_CONCEPT";
    public static final String VOID_REASON = "updated since by lab technician";
    private static final String RESULT_TYPE_NUMERIC = "N";
    private static final String REFERRED_OUT = "REFERRED_OUT";

    private final ConceptService conceptService;
    private Concept labConcepts = null;

    public ResultObsHelper(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    public Obs createNewObsForOrder(OpenElisTestDetail testDetail, Order testOrder, Encounter resultEncounter) throws ParseException {
        Date obsDate = DateTime.parse(testDetail.getDateTime()).toDate();
        if(testDetail.getPanelUuid() != null) {
            Obs panelObs = createOrFindPanelObs(testDetail, testOrder, resultEncounter, obsDate);
            Concept testConcept = conceptService.getConceptByUuid(testDetail.getTestUuid());
            panelObs.addGroupMember(createNewTestObsForOrder(testDetail, testOrder, testConcept, obsDate));
            return panelObs;
        } else {
            return createNewTestObsForOrder(testDetail, testOrder, testOrder.getConcept(), obsDate);
        }
    }

    public void voidObs(Obs obs, Date testDate) {
        obs.setVoided(true);
        obs.setVoidReason(VOID_REASON);
        obs.setDateVoided(testDate);
        final Set<Obs> groupMembers = obs.getGroupMembers();
        if ((groupMembers != null) && (groupMembers.size() > 0)) {
            for (Obs member : groupMembers) {
                voidObs(member, testDate);
            }
        }
    }

    private Obs createNewTestObsForOrder(OpenElisTestDetail testDetail, Order order, Concept concept, Date obsDate) throws ParseException {
        Obs topLevelObs = newParentObs(order, concept, obsDate);
        Obs labObs = newParentObs(order, concept, obsDate);
        topLevelObs.addGroupMember(labObs);

        if(StringUtils.isNotBlank(testDetail.getResult())) {
            labObs.addGroupMember(newChildObs(order, obsDate, concept, testDetail.getResult()));
            labObs.addGroupMember(newChildObs(order, obsDate, LAB_ABNORMAL, testDetail.getAbnormal().toString()));

            if (testDetail.getResultType().equals(RESULT_TYPE_NUMERIC) && hasRange(testDetail)) {
                labObs.addGroupMember(newChildObs(order, obsDate, LAB_MINNORMAL, testDetail.getMinNormal().toString()));
                labObs.addGroupMember(newChildObs(order, obsDate, LAB_MAXNORMAL, testDetail.getMaxNormal().toString()));
            }
        }
        if (testDetail.isReferredOut()) {
            labObs.addGroupMember(newChildObs(order, obsDate, REFERRED_OUT, null ));
        }
        final Set<String> notes = testDetail.getNotes();
        if (notes != null) {
            for (String note : notes) {
                if (StringUtils.isNotBlank(note)) {
                    labObs.addGroupMember(newChildObs(order, obsDate, LAB_NOTES, note));
                }
            }
        }
        return topLevelObs;
    }

    private boolean hasRange(OpenElisTestDetail testDetail) {
        return testDetail.getMinNormal() != null && testDetail.getMaxNormal() != null;
    }

    private Obs createOrFindPanelObs(OpenElisTestDetail testDetail, Order testOrder, Encounter resultEncounter, Date obsDate) {
        Obs panelObs = null;
        for (Obs obs : resultEncounter.getObsAtTopLevel(false)) {
            if(obs.getConcept().getUuid().equals(testDetail.getPanelUuid()) && obs.getOrder().getId().equals(testOrder.getId())){
                panelObs = obs;
                break;
            }
        }
        return panelObs != null ? panelObs : newParentObs(testOrder, testOrder.getConcept(), obsDate);
    }

    private Concept getLabConceptByName(String name) {
        if (this.labConcepts == null) {
            this.labConcepts = conceptService.getConceptByName(LABRESULTS_CONCEPT);
        }
        final List<Concept> members = this.labConcepts.getSetMembers();
        for (Concept concept : members) {
            if (concept != null && concept.getName().getName().equals(name)) {
                return concept;
            }
        }
        return null;
    }

    private Obs newParentObs(Order order, Concept concept, Date obsDate) {
        Obs labObs = new Obs();
        labObs.setConcept(concept);
        labObs.setOrder(order);
        labObs.setObsDatetime(obsDate);
        return labObs;
    }

    private Obs newChildObs(Order order, Date obsDate, String conceptName, String value) throws ParseException {
        Concept concept = getLabConceptByName(conceptName);
        Obs resultObs = newChildObs(order, obsDate, concept, value);
        return resultObs;
    }

    private Obs newChildObs(Order order, Date obsDate, Concept concept, String value) throws ParseException {
        Obs resultObs = new Obs();
        resultObs.setConcept(concept);
        setValue(value, resultObs);
        resultObs.setObsDatetime(obsDate);
        resultObs.setOrder(order);
        return resultObs;
    }

    private void setValue(String value, Obs resultObs) throws ParseException {
        if (value == null || value.isEmpty()) return;
        try {
            resultObs.setValueAsString(value);
        } catch (NumberFormatException e) {
            resultObs.setValueText(null);
        }
    }
}
