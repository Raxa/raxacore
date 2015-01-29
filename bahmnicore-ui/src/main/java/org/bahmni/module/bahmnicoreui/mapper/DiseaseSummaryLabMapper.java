package org.bahmni.module.bahmnicoreui.mapper;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.bahmni.module.bahmnicoreui.contract.ConceptValue;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiseaseSummaryLabMapper extends DiseaseSummaryMapper<List<LabOrderResult>> {

    public Map<String, Map<String, ConceptValue>> map(List<LabOrderResult> labOrderResults, String groupBy) {
        Map<String, Map<String, ConceptValue>> result = new LinkedHashMap<>();
        for (LabOrderResult labOrderResult : labOrderResults) {
            String startDateTime = (RESULT_TABLE_GROUP_BY_ENCOUNTER.equals(groupBy)) ?
                    DateFormatUtils.format(labOrderResult.getAccessionDateTime(),DATE_TIME_FORMAT) : DateFormatUtils.format(labOrderResult.getVisitStartTime(),DATE_FORMAT);
            String conceptName = labOrderResult.getTestName();
            if (conceptName != null) {
                addToResultTable(result, startDateTime, conceptName, labOrderResult.getResult(), labOrderResult.getAbnormal(), true);
            }
        }
        return result;
    }
}
