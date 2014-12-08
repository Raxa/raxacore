package org.bahmni.module.bahmnicoreui.mapper;

import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.openmrs.DrugOrder;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.text.SimpleDateFormat;
import java.util.*;

public class DiseaseSummaryMapper {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    public Map<String, Map<String, ConceptValue>> mapObservations(List<BahmniObservation> bahmniObservations) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            List<BahmniObservation> observationsfromConceptSet = new ArrayList<>();
            getLeafObservationsfromConceptSet(bahmniObservation,observationsfromConceptSet);
            for (BahmniObservation observation : observationsfromConceptSet) {
                String visitStartDateTime = getDateAsString(observation.getVisitStartDateTime());
                String conceptName = observation.getConcept().getShortName()==null ?  observation.getConcept().getName(): observation.getConcept().getShortName();
                addToResultTable(result, visitStartDateTime, conceptName,observation.getValue(),observation.isAbnormal());
            }
        }
        return result;
    }

    public Map<String, Map<String, ConceptValue>> mapDrugOrders(List<DrugOrder> drugOrders) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        for (DrugOrder drugOrder : drugOrders) {
            String visitStartDateTime = getDateAsString(drugOrder.getEncounter().getVisit().getStartDatetime());
            String conceptName = drugOrder.getConcept().getName().getName();
            addToResultTable(result,visitStartDateTime,conceptName,drugOrder.getDrug().getStrength(),null);
        }
        return result;
    }

    public Map<String, Map<String, ConceptValue>> mapLabResults(List<LabOrderResult> labOrderResults) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        for (LabOrderResult labOrderResult : labOrderResults) {
            String visitStartDateTime = getDateAsString(labOrderResult.getVisitStartTime());
            String conceptName = labOrderResult.getPanelName() == null ? labOrderResult.getTestName():labOrderResult.getPanelName();
            addToResultTable(result,visitStartDateTime,conceptName,labOrderResult.getResult(),labOrderResult.getAbnormal());
        }
        return result;
    }

    private String getDateAsString(Date startDatetime) {
        return simpleDateFormat.format(startDatetime);
    }

    private void addToResultTable(Map<String, Map<String, ConceptValue>> result, String visitStartDateTime, String conceptName, Object value, Boolean abnormal) {

        ConceptValue conceptValue = new ConceptValue();
        conceptValue.setValue(getObsValue(value));
        conceptValue.setAbnormal(abnormal);

        Map<String, ConceptValue> cellValue = getMapForKey(visitStartDateTime, result);
        cellValue.put(conceptName, conceptValue);
        result.put(visitStartDateTime, cellValue);
    }


    private String getObsValue(Object value) {
        if(value instanceof EncounterTransaction.Concept){
            return ((EncounterTransaction.Concept) value).getName();
        }
        return value.toString();
    }

    private void getLeafObservationsfromConceptSet(BahmniObservation bahmniObservation, List<BahmniObservation> observationsfromConceptSet) {
        if (bahmniObservation.getGroupMembers().size() > 0) {
            for (BahmniObservation groupMember : bahmniObservation.getGroupMembers())
                getLeafObservationsfromConceptSet(groupMember, observationsfromConceptSet);
        } else {
            if (!BahmniObservationMapper.ABNORMAL_CONCEPT_CLASS.equals(bahmniObservation.getConcept().getConceptClass())){
                observationsfromConceptSet.add(bahmniObservation);
            }
        }
    }

    private Map<String, ConceptValue> getMapForKey(String visitStartDateTime, Map<String, Map<String, ConceptValue>> result) {
        Map<String, ConceptValue> cellValues = result.get(visitStartDateTime);
        return cellValues != null? cellValues: new LinkedHashMap<String,ConceptValue>();
    }
}
