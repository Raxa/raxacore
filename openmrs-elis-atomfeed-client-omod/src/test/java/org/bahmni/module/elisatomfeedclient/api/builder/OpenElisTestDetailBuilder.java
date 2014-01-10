package org.bahmni.module.elisatomfeedclient.api.builder;

import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;

public class OpenElisTestDetailBuilder {

    private final OpenElisTestDetail testDetail;

    public OpenElisTestDetailBuilder() {
        testDetail = new OpenElisTestDetail();
        testDetail.setTestUuid("Test123");
    }

    public OpenElisTestDetail build() {
        return testDetail;
    }

    public OpenElisTestDetailBuilder withTestUuid(String testUuid) {
        testDetail.setTestUuid(testUuid);
        return this;
    }

    public OpenElisTestDetailBuilder withStatus(String status) {
        testDetail.setStatus(status);
        return this;
    }

    public OpenElisTestDetailBuilder withPanelUuid(String panelUuid) {
        testDetail.setPanelUuid(panelUuid);
        return this;
    }
}
