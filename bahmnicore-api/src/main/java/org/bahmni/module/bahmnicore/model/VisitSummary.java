package org.bahmni.module.bahmnicore.model;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.visit.contract.VisitResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitSummary {
    private Date encounterDateTime;
    private List<EncounterTransaction.Diagnosis> diagnoses = new ArrayList<>();
    private List<EncounterTransaction.Disposition> dispositions = new ArrayList<>();
    private EncounterTransaction.Provider provider;

    public VisitSummary(VisitResponse visitResponse) {
        List<EncounterTransaction> encounters = visitResponse.getEncounters();
        for (EncounterTransaction encounter : encounters) {
            if (encounter.getDiagnoses() != null) {
                diagnoses.addAll(encounter.getDiagnoses());
            }
            if (encounter.getDisposition() != null) {
                dispositions.add(encounter.getDisposition());
            }
            if (encounter.getProviders() != null && encounter.getProviders().size() > 0 ) {
                provider = encounter.getProviders().iterator().next();
            }
        }
    }

    public List<EncounterTransaction.Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<EncounterTransaction.Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public List<EncounterTransaction.Disposition> getDispositions() {
        return dispositions;
    }

    public void setDispositions(List<EncounterTransaction.Disposition> dispositions) {
        this.dispositions = dispositions;
    }

    public EncounterTransaction.Provider getProvider() {
        return provider;
    }
}
