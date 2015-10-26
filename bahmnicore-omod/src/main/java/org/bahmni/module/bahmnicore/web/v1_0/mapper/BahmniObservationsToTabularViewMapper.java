package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.pivottable.contract.PivotRow;
import org.openmrs.module.bahmniemrapi.pivottable.contract.PivotTable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BahmniObservationsToTabularViewMapper {
    public PivotTable constructTable(String groupByConcept, List<String> conceptNames, Collection<BahmniObservation> bahmniObservations) {
        PivotTable pivotTable = new PivotTable();
        if (bahmniObservations == null) {
            return pivotTable;
        }

        List<PivotRow> rows = new ArrayList<>();
        Set<String> headers = new LinkedHashSet<>();
        headers.add(groupByConcept);
        if (CollectionUtils.isNotEmpty(conceptNames)) {
            conceptNames.add(0, groupByConcept);
        }
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            rows.add(constructRow(bahmniObservation, conceptNames, headers));
        }

        pivotTable.setRows(rows);
        pivotTable.setHeaders(headers);
        return pivotTable;
    }

    private PivotRow constructRow(BahmniObservation bahmniObservation, List<String> conceptNames, Set<String> headers) {
        PivotRow row = new PivotRow();
        constructColumns(conceptNames, headers, row, bahmniObservation);
        return row;
    }

    private void constructColumns(List<String> conceptNames, Set<String> headers, PivotRow row, BahmniObservation observation) {
        if (observation.getConcept().isSet()) {
            if (observation.getClass().getName().equals("Concept Details")) {
                addColumn(conceptNames, headers, row, observation);
            }
            for (BahmniObservation bahmniObservation : observation.getGroupMembers()) {
                constructColumns(conceptNames, headers, row, bahmniObservation);
            }
        } else {
            addColumn(conceptNames, headers, row, observation);
        }
    }

    private void addColumn(List<String> conceptNames, Set<String> headers, PivotRow row, BahmniObservation observation) {
        if (conceptNames == null || conceptNames.equals(Collections.EMPTY_LIST) || conceptNames.contains(observation.getConcept().getName())) {
            headers.add(observation.getConcept().getName());
            row.addColumn(observation.getConcept().getName(), observation);
        }
    }

}
