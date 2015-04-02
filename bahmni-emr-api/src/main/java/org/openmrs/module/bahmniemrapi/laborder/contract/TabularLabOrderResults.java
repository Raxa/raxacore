package org.openmrs.module.bahmniemrapi.laborder.contract;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
public class TabularLabOrderResults {
    private List<DateLabel> dates = new ArrayList<>();
    private List<TestOrderLabel> orders = new ArrayList<>();
    private List<CoordinateValue> values = new ArrayList<>();

    @JsonCreator
    public TabularLabOrderResults(@JsonProperty("dates")List<DateLabel> dates,
                                  @JsonProperty("orders")List<TestOrderLabel> orders,
                                  @JsonProperty("values")List<CoordinateValue> values) {
        this.dates = dates;
        this.orders = orders;
        this.values = values;
    }

    @Data
    public static class DateLabel {
        private Integer index;
        private String date;

        @JsonCreator
        public DateLabel(@JsonProperty("index")Integer index,
                         @JsonProperty("date")String date) {
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

        @JsonCreator
        public TestOrderLabel(@JsonProperty("index")Integer index,
                              @JsonProperty("testName")String testName,
                              @JsonProperty("minNormal")Double minNormal,
                              @JsonProperty("maxNormal")Double maxNormal,
                              @JsonProperty("testUnitOfMeasurement")String testUnitOfMeasurement) {
            this.index = index;
            this.testName = testName;
            this.minNormal = minNormal;
            this.maxNormal = maxNormal;
            this.testUnitOfMeasurement = testUnitOfMeasurement;
        }
    }


    @Data
    public static class CoordinateValue {
        private Date accessionDateTime;
        private Integer dateIndex;
        private Integer testOrderIndex;
        private String result;
        private Boolean abnormal;
        private Boolean referredOut;
        private String uploadedFileName;
    }
}
