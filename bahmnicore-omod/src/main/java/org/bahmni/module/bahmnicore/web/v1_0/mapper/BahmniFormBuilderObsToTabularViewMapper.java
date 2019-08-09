package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.pivottable.contract.PivotRow;
import org.openmrs.module.bahmniemrapi.pivottable.contract.PivotTable;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bahmni.module.bahmnicore.forms2.util.FormUtil.getFormNameAlongWithVersion;

@Component
public class BahmniFormBuilderObsToTabularViewMapper extends BahmniObservationsToTabularViewMapper {
    public PivotTable constructTable(Set<EncounterTransaction.Concept> concepts,
                                     Collection<BahmniObservation> bahmniObservations, String groupByConcept) {
        PivotTable pivotTable = new PivotTable();
        if (bahmniObservations == null) {
            return pivotTable;
        }
        List<PivotRow> rows = constructRows(concepts, bahmniObservations, groupByConcept);

        pivotTable.setHeaders(concepts);
        pivotTable.setRows(rows);
        return pivotTable;
    }

    public List<PivotRow> getNonEmptyRows(List<PivotRow> rows, String groupByConceptName){
        return rows.stream().filter(row -> isNonNullRow(groupByConceptName, row)).collect(Collectors.toList());
    }

    private List<PivotRow> constructRows(Set<EncounterTransaction.Concept> concepts,
                                         Collection<BahmniObservation> bahmniObservations, String groupByConceptName) {
        Map<String, List<BahmniObservation>> rowsMapper = getRowsMapper(bahmniObservations);
        List<PivotRow> rows = new ArrayList<>();
        rowsMapper.forEach((rowIdentifier, rowObservations) -> {
            PivotRow row = new PivotRow();
            rowObservations.forEach(observation -> addColumn(concepts, row, observation));
            if (row.getColumns().containsKey(groupByConceptName)) {
                rows.add(row);
            }
        });
        return rows;
    }

    private Map<String, List<BahmniObservation>> getRowsMapper(Collection<BahmniObservation> bahmniObservations) {
        Map<String, List<BahmniObservation>> rowsMapper = new LinkedHashMap<>();
        bahmniObservations.forEach(bahmniObservation -> {
            String rowIdentifier = getRowIdentifier(bahmniObservation);
            List<BahmniObservation> bahmniObs;
            bahmniObs = rowsMapper.containsKey(rowIdentifier) ? rowsMapper.get(rowIdentifier) : new ArrayList<>();
            bahmniObs.add(bahmniObservation);
            rowsMapper.put(rowIdentifier, bahmniObs);
        });
        return rowsMapper;
    }

    private String getRowIdentifier(BahmniObservation bahmniObservation) {
        return bahmniObservation.getEncounterUuid() + getFormNameAlongWithVersion(bahmniObservation.getFormFieldPath());
    }
}
