package org.openmrs.module.bahmniemrapi.laborder.contract;

import lombok.Data;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class LabOrderResults {
    private List<LabOrderResult> results = new ArrayList<>();
    private TabularLabOrderResults tabularResult;

    public LabOrderResults(List<LabOrderResult> results) {
        this.results = results;
        this.tabularResult = this.tabulate();
    }

    private TabularLabOrderResults tabulate() {
        Map<LocalDate, TabularLabOrderResults.DateLabel> dateMap = new TreeMap<>();
        Map<String, TabularLabOrderResults.TestOrderLabel> orderMap = new TreeMap<>();
        List<TabularLabOrderResults.CoordinateValue> coordinateValues = new ArrayList<>();

        Integer dateLabelIndexCounter = 0;
        Integer testOrderLabelCounter = 0;

        for (LabOrderResult result : results) {
            LocalDate orderDate = new LocalDate(result.getAccessionDateTime());
            if(dateMap.get(orderDate) == null) {
                dateMap.put(orderDate, new TabularLabOrderResults.DateLabel(dateLabelIndexCounter++, orderDate.toString("dd-MMM-yyyy")));
            }
            if(orderMap.get(result.getTestName()) == null) {
                orderMap.put(result.getTestName(), new TabularLabOrderResults.TestOrderLabel(testOrderLabelCounter++, result.getTestName(), result.getMinNormal(), result.getMaxNormal(), result.getTestUnitOfMeasurement()));
            }

            if(result.getResult() != null || result.getReferredOut() == true) {
                TabularLabOrderResults.CoordinateValue coordinateValue = new TabularLabOrderResults.CoordinateValue();
                coordinateValue.setDateIndex(dateMap.get(orderDate).getIndex());
                coordinateValue.setTestOrderIndex(orderMap.get(result.getTestName()).getIndex());
                coordinateValue.setResult(result.getResult());
                coordinateValue.setAbnormal(result.getAbnormal());
                coordinateValue.setReferredOut(result.getReferredOut());
                coordinateValues.add(coordinateValue);
            }
        }

        return new TabularLabOrderResults(new ArrayList<>(dateMap.values()), new ArrayList<>(orderMap.values()), coordinateValues);
    }
}
