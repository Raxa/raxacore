package org.openmrs.module.bahmniemrapi.encountertransaction.service;

import org.joda.time.DateTime;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RetrospectiveEncounterTransactionService {
    protected final VisitMatcher visitMatcher;

    @Autowired
    public RetrospectiveEncounterTransactionService(VisitMatcher visitMatcher) {
        this.visitMatcher = visitMatcher;
    }

    public BahmniEncounterTransaction updatePastEncounters(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate) {
        if (!BahmniEncounterTransaction.isRetrospectiveEntry(bahmniEncounterTransaction.getEncounterDateTime())) {
            return bahmniEncounterTransaction;
        }

        Visit matchingVisit = visitMatcher.getVisitFor(patient, bahmniEncounterTransaction.getVisitType(),
                bahmniEncounterTransaction.getEncounterDateTime(), visitStartDate, visitEndDate, bahmniEncounterTransaction.getLocationUuid());
        bahmniEncounterTransaction.setVisitUuid(matchingVisit.getUuid());

        // TODO : Mujir - this should not happen here. Just set the visitType. BahmniEncounterTransaction should handle string visitTypes.
        bahmniEncounterTransaction.setVisitTypeUuid(matchingVisit.getVisitType().getUuid());

        return bahmniEncounterTransaction.updateForRetrospectiveEntry(bahmniEncounterTransaction.getEncounterDateTime());
    }
}

class DateUtils {

    public static Boolean isBefore(Date date1, Date date2) {
        return new DateTime(date1).toDateMidnight().isBefore(new DateTime(date2).toDateMidnight());
    }

}
