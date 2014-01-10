package org.bahmni.module.elisatomfeedclient.api.domain;

import java.util.HashSet;
import java.util.Set;

public class AccessionDiff {
    private Set<OpenElisTestDetail> addedTestDetails = new HashSet<>();
    private Set<OpenElisTestDetail> removedTestDetails = new HashSet<>();

    public Set<OpenElisTestDetail> getAddedTestDetails() {
        return addedTestDetails;
    }

    public void addAddedTestDetail(OpenElisTestDetail testDetail) {
        addedTestDetails.add(testDetail);
    }

    public Set<OpenElisTestDetail> getRemovedTestDetails() {
        return removedTestDetails;
    }

    public void addRemovedTestDetails(OpenElisTestDetail testDetail) {
        removedTestDetails.add(testDetail);
    }
}
