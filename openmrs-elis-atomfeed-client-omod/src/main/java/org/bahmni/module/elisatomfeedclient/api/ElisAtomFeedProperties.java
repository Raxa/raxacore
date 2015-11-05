package org.bahmni.module.elisatomfeedclient.api;

import org.bahmni.module.bahmnicore.properties.BahmniCoreProperties;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElisAtomFeedProperties extends AtomFeedProperties {

    private static final String OPEN_ELIS_URI = "openelis.uri";
    private static final String CONNECT_TIMEOUT = "feed.connectionTimeoutInMilliseconds";
    private static final String MAX_FAILED_EVENTS = "feed.maxFailedEvents";
    private static final String READ_TIMEOUT = "feed.replyTimeoutInMilliseconds";
    private static final String LAB_SYSTEM_USERNAME = "openmrs.labSystem.username";
    private static final String ORDER_TYPE_LAB_ORDER = "openmrs.orderType.labOrder";
    private static final String ENCOUNTER_TYPE_INVESTIGATION = "openmrs.encounterType.investigation";
    private static final String LAB_SYSTEM_PROVIDER_IDENTIFIER = "openmrs.labSystem.identifier";
    public static final String ENCOUNTER_TYPE_LAB_RESULT = "openmrs.encounterType.labResult";

    public static final String DEFAULT_INVESTIGATION_ENCOUNTER_TYPE = "INVESTIGATION";
    public static final String DEFAULT_LAB_SYSTEM_USERNAME = "Lab System";
    public static final String DEFAULT_LAB_ORDER_TYPE = "Order";
    public static final String DEFAULT_LAB_SYSTEM_IDENTIFIER = "LABSYSTEM";
    public static final String DEFAULT_LAB_RESULT_ENCOUNTER_TYPE = "LAB_RESULT";



    public String getPatientFeedUri() {
        return BahmniCoreProperties.getProperty("patient.feed.uri");
    }

    public String getOpenElisUri() {
        return BahmniCoreProperties.getProperty(OPEN_ELIS_URI);
    }

    @Override
    public int getMaxFailedEvents() {
        return Integer.parseInt(BahmniCoreProperties.getProperty(MAX_FAILED_EVENTS));
    }

    @Override
    public int getReadTimeout() {
        return Integer.parseInt(BahmniCoreProperties.getProperty(READ_TIMEOUT));
    }

    @Override
    public int getConnectTimeout() {
        return Integer.parseInt(BahmniCoreProperties.getProperty(CONNECT_TIMEOUT));
    }

    public String getEncounterTypeInvestigation() {
        String property = BahmniCoreProperties.getProperty(ENCOUNTER_TYPE_INVESTIGATION);
        return property == null ? DEFAULT_INVESTIGATION_ENCOUNTER_TYPE : property;
    }

    public String getLabSystemUserName() {
        String property = BahmniCoreProperties.getProperty(LAB_SYSTEM_USERNAME);
        return property == null ? DEFAULT_LAB_SYSTEM_USERNAME : property;
    }

    public String getOrderTypeLabOrderName() {
        String property = BahmniCoreProperties.getProperty(ORDER_TYPE_LAB_ORDER);
        return property == null ? DEFAULT_LAB_ORDER_TYPE : property;
    }

    public String getLabSystemIdentifier() {
        String property = BahmniCoreProperties.getProperty(LAB_SYSTEM_PROVIDER_IDENTIFIER);
        return property == null ? DEFAULT_LAB_SYSTEM_IDENTIFIER : property;
    }

    public String getEncounterTypeForLabResult() {
        String property = BahmniCoreProperties.getProperty(ENCOUNTER_TYPE_LAB_RESULT);
        return property == null ? DEFAULT_LAB_RESULT_ENCOUNTER_TYPE : property;
    }
}
