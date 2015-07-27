package org.bahmni.module.bahmnicore.web.v1_0;

import org.bahmni.test.web.controller.BaseWebControllerTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BaseIntegrationTest extends BaseWebControllerTest {
}
