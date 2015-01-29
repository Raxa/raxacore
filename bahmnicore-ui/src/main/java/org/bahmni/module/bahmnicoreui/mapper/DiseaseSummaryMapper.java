package org.bahmni.module.bahmnicoreui.mapper;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DiseaseSummaryMapper<T> {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String RESULT_TABLE_GROUP_BY_ENCOUNTER = "encounters";
    public static final String RESULT_TABLE_GROUP_BY_VISITS = "visits";


    protected abstract Map<String, Map<String, ConceptValue>> map(T patientData ,String groupBy);

    protected String getEmptyIfNull(Object text) {
        return text == null ? "" : text.toString();
    }

    protected void addToResultTable(Map<String, Map<String, ConceptValue>> result, String startDateTime, String conceptName, Object value, Boolean abnormal, boolean replaceExisting) {
        Map<String, ConceptValue> cellValue = getMapForKey(startDateTime, result);
        if (cellValue.containsKey(conceptName) && !replaceExisting) return;

        ConceptValue conceptValue = new ConceptValue();
        conceptValue.setValue(getObsValueAsString(value));
        conceptValue.setAbnormal(abnormal);
        cellValue.put(conceptName, conceptValue);
        result.put(startDateTime, cellValue);
    }

    protected String getObsValueAsString(Object value) {
        if (value != null) {
            if (value instanceof EncounterTransaction.Concept) {
                EncounterTransaction.Concept concept = (EncounterTransaction.Concept) value;
                return (concept.getShortName() == null ? concept.getName() : concept.getShortName());
            } else if (value instanceof Boolean) {
                return (Boolean) value ? "Yes" : "No";
            }
            return String.valueOf(value);
        }
        return "";
    }

    protected Map<String, ConceptValue> getMapForKey(String visitStartDateTime, Map<String, Map<String, ConceptValue>> result) {
        Map<String, ConceptValue> cellValues = result.get(visitStartDateTime);
        return cellValues != null ? cellValues : new LinkedHashMap<String, ConceptValue>();
    }

    protected String getGroupByDate(BahmniObservation observation, String groupBy) {
        return (RESULT_TABLE_GROUP_BY_ENCOUNTER.equals(groupBy) ?
                DateFormatUtils.format(observation.getEncounterDateTime(), DATE_TIME_FORMAT) : DateFormatUtils.format(observation.getVisitStartDateTime(), DATE_FORMAT));
    }

    protected String getConceptNameToDisplay(BahmniObservation observation) {
        return observation.getConcept().getShortName() == null ? observation.getConcept().getName() : observation.getConcept().getShortName();
    }
}
