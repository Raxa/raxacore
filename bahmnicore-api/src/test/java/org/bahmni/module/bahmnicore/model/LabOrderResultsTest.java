package org.bahmni.module.bahmnicore.model;

import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResult;
import org.bahmni.module.bahmnicore.model.BahmniVisit.LabOrderResults;
import org.bahmni.module.bahmnicore.model.BahmniVisit.TabularLabOrderResults;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LabOrderResultsTest {
    @Test
    public void shouldCreateSparseMatrixForLabOrderResultAndDates() throws Exception {
        List<LabOrderResult> results = Arrays.asList(
            new LabOrderResult("Haemoglobin", "ppm", 15.0, 20.0, DateUtil.getDateTime(2014, 2, 10), "17.0", false),
            new LabOrderResult("Haemoglobin", "ppm", 15.0, 20.0, DateUtil.getDateTime(2014, 2, 12), "19.0", false),
            new LabOrderResult("Haemoglobin", "ppm", 15.0, 20.0, DateUtil.getDateTime(2014, 1, 14, 0, 0, 1, 0), "9.0", true),
            new LabOrderResult("Haemoglobin", "ppm", 15.0, 20.0, DateUtil.getDateTime(2014, 1, 14, 1, 0, 0, 0), "9.2", true),
            new LabOrderResult("ESR", "gm/L", 100.0, 200.0, DateUtil.getDateTime(2014, 5, 15), "50.0", false),
            new LabOrderResult("ESR", "gm/L", 100.0, 200.0, DateUtil.getDateTime(2014, 5, 16), "51.0", false)
        );

        LabOrderResults labOrderResults = new LabOrderResults(results);
        TabularLabOrderResults table = labOrderResults.getTabularResult();

        assertEquals(5, table.getDates().size());
        assertEquals(2, table.getOrders().size());
        assertEquals(6, table.getValues().size());
    }
}
