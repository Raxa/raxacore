package org.bahmni.module.bahmnicoreui.mapper;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.bahmni.module.bahmnicoreui.constant.DiseaseSummaryConstants;
import org.bahmni.module.bahmnicoreui.contract.DiseaseSummaryMap;
import org.openmrs.module.bahmniemrapi.laborder.contract.LabOrderResult;

import java.util.List;

public class DiseaseSummaryLabMapper {

    public DiseaseSummaryMap map(List<LabOrderResult> labOrderResults, String groupBy) {
        DiseaseSummaryMap diseaseSummaryMap = new DiseaseSummaryMap();
        for (LabOrderResult labOrderResult : labOrderResults) {
            String startDateTime = (DiseaseSummaryConstants.RESULT_TABLE_GROUP_BY_ENCOUNTER.equals(groupBy)) ?
                    DateFormatUtils.format(labOrderResult.getAccessionDateTime(), DiseaseSummaryConstants.DATE_TIME_FORMAT) : DateFormatUtils.format(labOrderResult.getVisitStartTime(), DiseaseSummaryConstants.DATE_FORMAT);
            String conceptName = labOrderResult.getTestName();
            if (conceptName != null) {
                diseaseSummaryMap.put(startDateTime, conceptName, labOrderResult.getResult(), labOrderResult.getAbnormal(), true);
            }
        }
        return diseaseSummaryMap;
    }
}
