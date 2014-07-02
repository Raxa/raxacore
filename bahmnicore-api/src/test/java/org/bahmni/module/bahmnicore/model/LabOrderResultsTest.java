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
            new LabOrderResult("uuid1", DateUtil.getDateTime(2014, 2, 10), "Haemoglobin", "ppm", 15.0, 20.0, "17.0", false, false),
            new LabOrderResult("uuid1", DateUtil.getDateTime(2014, 2, 12), "Haemoglobin", "ppm", 15.0, 20.0, "19.0", false, false),
            new LabOrderResult("uuid1", DateUtil.getDateTime(2014, 1, 14, 0, 0, 1, 0), "Haemoglobin", "ppm", 15.0, 20.0, "9.0", true, false),
            new LabOrderResult("uuid1", DateUtil.getDateTime(2014, 1, 14, 1, 0, 0, 0), "Haemoglobin", "ppm", 15.0, 20.0, "9.2", true, false),
            new LabOrderResult("uuid2", DateUtil.getDateTime(2014, 5, 15), "ESR", "gm/L", 100.0, 200.0, "50.0", false, false),
            new LabOrderResult("uuid2", DateUtil.getDateTime(2014, 5, 16), "ESR", "gm/L", 100.0, 200.0, "51.0", false, false),
            new LabOrderResult("uuid3", DateUtil.getDateTime(2014, 5, 17), "ESR", null, null, null, null, null, false),
            new LabOrderResult("uuid3", DateUtil.getDateTime(2014, 5, 18), "ESR", null, null, null, null, null, true)
        );

        LabOrderResults labOrderResults = new LabOrderResults(results);
        TabularLabOrderResults table = labOrderResults.getTabularResult();

        assertEquals(7, table.getDates().size());
        assertEquals(2, table.getOrders().size());
        assertEquals(7, table.getValues().size());
    }
}
