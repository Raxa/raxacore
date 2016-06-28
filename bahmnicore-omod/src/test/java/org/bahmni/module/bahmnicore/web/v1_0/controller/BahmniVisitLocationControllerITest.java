package org.bahmni.module.bahmnicore.web.v1_0.controller;


import org.bahmni.module.bahmnicore.web.v1_0.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class BahmniVisitLocationControllerITest extends BaseIntegrationTest {

    @Autowired
    private BahmniVisitLocationController bahmniLocationController;

    @Before
    public void setUp() throws Exception {
        executeDataSet("locationData.xml");
    }

    @Test
    public void shouldGetImmediateParentLocationIdIfItIsTaggedToVisitLocation() throws Exception {
        String locationUuid = bahmniLocationController.getVisitLocationInfo("c36006e5-9fbb-4f20-866b-0ece245615a1");
        assertEquals("e36006e5-9fbb-4f20-866b-0ece24561525", locationUuid);
    }

    @Test
    public void shouldTraverseTillItGetsParentLocationIdWhichIsTaggedToVisitLocation() throws Exception {
        String locationUuid = bahmniLocationController.getVisitLocationInfo("e36023e5-9fwb-4f20-866b-0ece24561525");
        assertEquals("e36006e5-9fbb-4f20-866b-0ece24561525", locationUuid);
    }

    @Test
    public void shouldTraverseTillRootIFNoneOfLocationsAreTaggedToVisitLocation() throws Exception {
        String locationUuid = bahmniLocationController.getVisitLocationInfo("l36023e5-9fhb-4f20-866b-0ece24561525");
        assertEquals("l38923e5-9fhb-4f20-866b-0ece24561525", locationUuid);
    }
}