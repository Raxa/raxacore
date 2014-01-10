package org.bahmni.module.elisatomfeedclient.api.builder;


import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisAccession;
import org.bahmni.module.elisatomfeedclient.api.domain.OpenElisTestDetail;

import java.util.Set;

public class OpenElisAccessionBuilder {

    private OpenElisAccession openElisAccession;

    public OpenElisAccessionBuilder() {
        openElisAccession = new OpenElisAccession();
        openElisAccession.setAccessionUuid("1234");
        openElisAccession.addTestDetail(new OpenElisTestDetailBuilder().build());
    }

    public OpenElisAccession build() {
        return openElisAccession;
    }

    public OpenElisAccessionBuilder withTestDetails(Set<OpenElisTestDetail> testDetails) {
        openElisAccession.setTestDetails(testDetails);
        return this;
    }
}
