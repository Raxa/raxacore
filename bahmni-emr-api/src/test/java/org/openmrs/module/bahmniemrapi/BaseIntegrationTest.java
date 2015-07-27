package org.openmrs.module.bahmniemrapi;

import org.openmrs.test.BaseModuleContextSensitiveTest;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BaseIntegrationTest extends BaseModuleContextSensitiveTest {
}
