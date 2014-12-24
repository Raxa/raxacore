package org.openmrs.module.bahmniemrapi.encountertransaction.service;

import org.joda.time.DateTime;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.openmrs.module.bahmniemrapi.encountertransaction.service.DateUtils.isBefore;

@Component
public class RetrospectiveEncounterTransactionService {
    protected final VisitIdentificationHelper visitIdentificationHelper;

    @Autowired
    public RetrospectiveEncounterTransactionService(VisitIdentificationHelper visitIdentificationHelper) {
        this.visitIdentificationHelper = visitIdentificationHelper;
    }

    public BahmniEncounterTransaction updatePastEncounters(BahmniEncounterTransaction bahmniEncounterTransaction, Patient patient, Date visitStartDate, Date visitEndDate) {
        if (bahmniEncounterTransaction.getEncounterDateTime() == null || !isBefore(bahmniEncounterTransaction.getEncounterDateTime(), new Date())) {
            return bahmniEncounterTransaction;
        }

        Visit matchingVisit = visitIdentificationHelper.getVisitFor(patient, bahmniEncounterTransaction.getVisitType(),
                bahmniEncounterTransaction.getEncounterDateTime(), visitStartDate, visitEndDate);
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
