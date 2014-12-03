package org.bahmni.module.bahmnicoreui.mapper;

import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.BahmniObservationMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiseaseSummaryMapper {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    public Map<String, Map<String, ConceptValue>> mapObservations(List<BahmniObservation> bahmniObservations) {
        Map<String, Map<String, ConceptValue>> result = new HashMap();

        for (BahmniObservation bahmniObservation : bahmniObservations) {
            List<BahmniObservation> observationsfromConceptSet = new ArrayList<>();
            getLeafObservationsfromConceptSet(bahmniObservation,observationsfromConceptSet);
            for (BahmniObservation observation : observationsfromConceptSet) {

                String visitStartDateTime = simpleDateFormat.format(observation.getVisitStartDateTime());
                String conceptName = observation.getConcept().getShortName()==null ?  observation.getConcept().getName(): observation.getConcept().getShortName();

                ConceptValue conceptValue = new ConceptValue();
                conceptValue.setValue(observation.getValue().toString());
                conceptValue.setAbnormal(observation.getIsAbnormal());

                Map<String, ConceptValue> cellValue = getMapForKey(visitStartDateTime, result);
                cellValue.put(conceptName, conceptValue);
                result.put(visitStartDateTime, cellValue);
            }

        }
        return result;
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
        return cellValues != null? cellValues: new HashMap<String,ConceptValue>();
    }
}
