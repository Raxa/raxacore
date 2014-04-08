package org.bahmni.module.elisatomfeedclient.api.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class OpenElisAccession {
    private String accessionUuid;
    private String patientUuid;
    private String patientFirstName;
    private String patientLastName;
    private String dateTime;
    private String patientIdentifier;
    private List<String> accessionNotes;
    private Set<OpenElisTestDetail> testDetails = new HashSet<>();

    public void addTestDetail(OpenElisTestDetail testDetail) {
        getTestDetails().add(testDetail);
    }

    public AccessionDiff getDiff(Encounter previousEncounter) {
        AccessionDiff accessionDiff = new AccessionDiff();
        for (OpenElisTestDetail testDetail : testDetails) {
            String orderableUuid = StringUtils.isBlank(testDetail.getPanelUuid()) ? testDetail.getTestUuid() : testDetail.getPanelUuid();
            if (testDetail.isCancelled()) {
                if (hasOrderByUuid(previousEncounter.getOrders(), orderableUuid)) {
                    accessionDiff.addRemovedTestDetails(testDetail);
                }
            } else {
                if (!hasOrderByUuid(previousEncounter.getOrders(), orderableUuid)) {
                    accessionDiff.addAddedTestDetail(testDetail);
                }
            }
        }

        return accessionDiff;
    }

    private boolean hasOrderByUuid(Set<Order> orders, String testUuid) {
        for (Order order : orders) {
            if (!order.getVoided() && order.getConcept() != null && order.getConcept().getUuid().equals(testUuid))
                return true;
        }
        return false;
    }

    public Date fetchDate() {
        return dateTime == null ? null : DateTime.parse(dateTime).toDate();
    }

    public String getHealthCenter() {
        return patientIdentifier.substring(0, 3);
    }

    public AccessionDiff getAccessionNoteDiff(Encounter encounter, Concept labManagerNoteConcept) {
        AccessionDiff accessionNotesDiff = new AccessionDiff();
        if (accessionNotes != null) {
            List<String> accessionNotesCopy = new ArrayList<>(accessionNotes);
            filterOutAlreadyAddedAccessionNotes(encounter, labManagerNoteConcept, accessionNotesCopy);
            accessionNotesDiff.setAccessionNotesToBeAdded(accessionNotesCopy);
        }
        return accessionNotesDiff;
    }

    private void filterOutAlreadyAddedAccessionNotes(Encounter encounter, Concept labManagerNoteConcept, List<String> accessionNotesCopy) {
        Set<Obs> encObs = encounter.getObs();
        for (Obs obs : encObs) {
            if (obs.getConcept().equals(labManagerNoteConcept)) {
                for (String accessionNote : accessionNotes) {
                    if (accessionNote.equals(obs.getValueText())) {
                        accessionNotesCopy.remove(accessionNote);
                    }
                }
            }
        }
    }


}
