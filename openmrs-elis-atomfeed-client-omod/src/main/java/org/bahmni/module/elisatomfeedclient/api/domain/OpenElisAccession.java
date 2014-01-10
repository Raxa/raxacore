package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Order;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
public class OpenElisAccession {
    private String accessionUuid;
    private String patientUuid;
    private String patientFirstName;
    private String patientLastName;
    private Date datetime;
    private Set<OpenElisTestDetail> testDetails = new HashSet<>();

    public void addTestDetail(OpenElisTestDetail testDetail){
        getTestDetails().add(testDetail);
    }

    public AccessionDiff getDiff(Encounter previousEncounter) {
        AccessionDiff accessionDiff = new AccessionDiff();
        for (OpenElisTestDetail testDetail : testDetails) {
            String orderableUuid = StringUtils.isBlank(testDetail.getPanelUuid()) ? testDetail.getTestUuid() : testDetail.getPanelUuid();
            if(testDetail.isCancelled()) {
                if(hasOrderByUuid(previousEncounter.getOrders(), orderableUuid)) {
                    accessionDiff.addRemovedTestDetails(testDetail);
                }
            }
            else {
                if(!hasOrderByUuid(previousEncounter.getOrders(), orderableUuid)) {
                    accessionDiff.addAddedTestDetail(testDetail);
                }
            }
        }

        return accessionDiff;
    }

    private boolean hasOrderByUuid(Set<Order> orders, String testUuid) {
        for (Order order : orders) {
            if (!order.getVoided() && order.getConcept().getUuid().equals(testUuid))
                return true;
        }
        return false;
    }
}
