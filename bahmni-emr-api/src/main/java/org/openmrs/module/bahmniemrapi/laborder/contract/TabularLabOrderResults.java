package org.openmrs.module.bahmniemrapi.laborder.contract;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TabularLabOrderResults {
    private List<DateLabel> dates = new ArrayList<>();
    private List<TestOrderLabel> orders = new ArrayList<>();
    private List<CoordinateValue> values = new ArrayList<>();

    public TabularLabOrderResults(List<DateLabel> dates, List<TestOrderLabel> orders, List<CoordinateValue> values) {
        this.dates = dates;
        this.orders = orders;
        this.values = values;
    }

    @Data
    public static class DateLabel {
        private Integer index;
        private String date;

        public DateLabel(Integer index, String date) {
            this.index = index;
            this.date = date;
        }
    }

    @Data
    public static class TestOrderLabel {
        private Integer index;
        private String testName;
        private Double minNormal;
        private Double maxNormal;
        private String testUnitOfMeasurement;

        public TestOrderLabel(Integer index, String testName, Double minNormal, Double maxNormal, String testUnitOfMeasurement) {
            this.index = index;
            this.testName = testName;
            this.minNormal = minNormal;
            this.maxNormal = maxNormal;
            this.testUnitOfMeasurement = testUnitOfMeasurement;
        }
    }


    @Data
    public static class CoordinateValue {
        private Integer dateIndex;
        private Integer testOrderIndex;
        private String result;
        private Boolean abnormal;
        private Boolean referredOut;
    }
}
