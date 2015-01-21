package org.bahmni.module.bahmnicoreui.mapper;

import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiseaseSummaryMapper {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String RESULT_TABLE_GROUP_BY_ENCOUNTER = "encounters";
    public static final String RESULT_TABLE_GROUP_BY_VISITS = "visits";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

    public Map<String, Map<String, ConceptValue>> mapObservations(Collection<BahmniObservation> bahmniObservations, String groupBy) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        if(bahmniObservations != null){
            for (BahmniObservation bahmniObservation : bahmniObservations) {
                List<BahmniObservation> observationsFromConceptSet = new ArrayList<>();
                getLeafObservationsfromConceptSet(bahmniObservation,observationsFromConceptSet);
                for (BahmniObservation observation : observationsFromConceptSet) {
                    String startDateTime = (RESULT_TABLE_GROUP_BY_ENCOUNTER.equals(groupBy) ?
                            getDateTimeAsString(observation.getEncounterDateTime()) : getDateAsString(observation.getVisitStartDateTime()));
                    String conceptName = observation.getConcept().getShortName()==null ?  observation.getConcept().getName(): observation.getConcept().getShortName();
                    addToResultTable(result, startDateTime, conceptName, observation.getValue(), observation.isAbnormal(), false);
                }
            }
        }
        return result;
    }



    public Map<String, Map<String, ConceptValue>> mapDrugOrders(List<DrugOrder> drugOrders, String groupBy) throws IOException {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        for (DrugOrder drugOrder : drugOrders) {
            String startDateTime = (RESULT_TABLE_GROUP_BY_ENCOUNTER.equals(groupBy)) ?
                    getDateTimeAsString(drugOrder.getEncounter().getEncounterDatetime()) : getDateAsString(drugOrder.getEncounter().getVisit().getStartDatetime());
            String conceptName = drugOrder.getDrug().getConcept().getName().getName();
            String drugOrderValue = formattedDrugOrderValue(drugOrder);
            addToResultTable(result,startDateTime,conceptName, drugOrderValue,null,false);
        }
        return result;
    }

    public Map<String, Map<String, ConceptValue>> mapLabResults(List<LabOrderResult> labOrderResults, String groupBy) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        for (LabOrderResult labOrderResult : labOrderResults) {
            String startDateTime = (RESULT_TABLE_GROUP_BY_ENCOUNTER.equals(groupBy)) ?
                    getDateTimeAsString(labOrderResult.getAccessionDateTime()) : getDateAsString(labOrderResult.getVisitStartTime());
            String conceptName = labOrderResult.getTestName();
            if(conceptName != null){
                addToResultTable(result,startDateTime,conceptName,labOrderResult.getResult(),labOrderResult.getAbnormal(),true);
            }
        }
        return result;
    }

    private String formattedDrugOrderValue(DrugOrder drugOrder) throws IOException {
        String strength = drugOrder.getDrug().getStrength();
        Concept doseUnitsConcept = drugOrder.getDoseUnits();
        String doseUnit = doseUnitsConcept == null ? "" : " "+doseUnitsConcept.getName().getName();
        String dose = drugOrder.getDose()==null?"":drugOrder.getDose() + doseUnit;
        String frequency = getFrequency(drugOrder);
        String asNeeded = drugOrder.getAsNeeded()?"SOS":null;
        return concat(",",strength,dose,frequency,asNeeded);
    }

    private String getFrequency(DrugOrder drugOrder) throws IOException {
        if(drugOrder.getFrequency() == null){
            String dosingInstructions = drugOrder.getDosingInstructions();
            Map<String, Object> instructions = hashMapForJson(dosingInstructions);
            return concat("-", getEmptyIfNull(instructions.get("morningDose")),getEmptyIfNull(instructions.get("afternoonDose")),getEmptyIfNull(instructions.get("eveningDose")));
        }
        return drugOrder.getFrequency().getName();
    }

    private Map<String, Object> hashMapForJson(String dosingInstructions) throws IOException {
        if(dosingInstructions == null || dosingInstructions.isEmpty()){
            return Collections.EMPTY_MAP;
        }
        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        return objectMapper.readValue(dosingInstructions, typeRef);
    }

    private String getEmptyIfNull(Object text) {
        return text == null? "":text.toString();
    }

    private String concat(String separator,String... values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                stringBuilder.append(separator).append(value);
            }
        }
        return stringBuilder.length() > 1 ? stringBuilder.substring(1) :"";
    }

    private String getDateAsString(Date startDatetime) {
        return simpleDateFormat.format(startDatetime);
    }

    private String getDateTimeAsString(Date startDatetime) {
        return simpleDateTimeFormat.format(startDatetime);
    }

    private void addToResultTable(Map<String, Map<String, ConceptValue>> result, String startDateTime, String conceptName, Object value, Boolean abnormal,boolean replaceExisting) {
        Map<String, ConceptValue> cellValue = getMapForKey(startDateTime, result);
        if(cellValue.containsKey(conceptName) && !replaceExisting) return;

        ConceptValue conceptValue = new ConceptValue();
        conceptValue.setValue(getObsValue(value));
        conceptValue.setAbnormal(abnormal);
        cellValue.put(conceptName, conceptValue);
        result.put(startDateTime, cellValue);
    }

    private String getObsValue(Object value) {
        if(value != null){
            if(value instanceof EncounterTransaction.Concept){
                EncounterTransaction.Concept concept = (EncounterTransaction.Concept) value;
                return (concept.getShortName() == null ? concept.getName() : concept.getShortName());
            }
            else if(value instanceof Boolean){
                return (Boolean)value?"Yes":"No";
            }
            return String.valueOf(value);
        }
        return "";
    }

    private void getLeafObservationsfromConceptSet(BahmniObservation bahmniObservation, List<BahmniObservation> observationsfromConceptSet) {
        if (bahmniObservation.getGroupMembers().size() > 0) {
            for (BahmniObservation groupMember : bahmniObservation.getGroupMembers())
                getLeafObservationsfromConceptSet(groupMember, observationsfromConceptSet);
        } else {
            if (!ETObsToBahmniObsMapper.ABNORMAL_CONCEPT_CLASS.equals(bahmniObservation.getConcept().getConceptClass())){
                observationsfromConceptSet.add(bahmniObservation);
            }
        }
    }

    private Map<String, ConceptValue> getMapForKey(String visitStartDateTime, Map<String, Map<String, ConceptValue>> result) {
        Map<String, ConceptValue> cellValues = result.get(visitStartDateTime);
        return cellValues != null? cellValues: new LinkedHashMap<String,ConceptValue>();
    }
}
