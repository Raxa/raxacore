package org.bahmni.module.admin.csv;

import org.bahmni.csv.MigrateResult;
import org.bahmni.csv.Stage;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ObsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class EncounterImporterIT extends BaseModuleContextSensitiveTest {
    @Autowired
    private EncounterImporter encounterImporter;

    @Before
    public void setUp() throws Exception {
        executeDataSet("dataSetup.xml");
    }

    @Test
    public void should_read_sample_csv_and_create_entity_list() throws Exception {
        String filePath = EncounterImporterIT.class.getResource("/").getPath();
        MigrateResult migrateResult = encounterImporter.importEncounters(filePath, "sample.csv");

        assertTrue("should have stopped at validation stage", migrateResult.isValidationStage());

        assertEquals(2, migrateResult.numberOfSuccessfulRecords());
        assertEquals(3, migrateResult.numberOfFailedRecords());
    }
}
