package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.observation.ObservationData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BahmniObservationsControllerIT extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniObservationsController bahmniObservationsController;

    @Ignore("mujir/mihir - work in progress")
    @Test
    public void get_observations_for_a_group_concept() throws Exception {
        executeDataSet("apiTestData.xml");
        List<ObservationData> observationDatas = bahmniObservationsController.get("86526ed5-3c11-11de-a0ba-001e378eb67a", 1, new String[]{"Blood Pressure"});
        assertEquals(2, observationDatas.size());
    }
}