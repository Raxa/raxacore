package org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters;

import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EncounterDetails {
    private String encounterUuid;
    private Date encounterDateTime;
    private Date visitDateTime;
    private Set<EncounterTransaction.Provider> providers = new HashSet<>();

    public EncounterDetails(String encounterUuid, Date encounterDateTime, Date visitDateTime) {
        this.encounterUuid = encounterUuid;
        this.encounterDateTime = encounterDateTime;
        this.visitDateTime = visitDateTime;
    }

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public void setEncounterUuid(String encounterUuid) {
        this.encounterUuid = encounterUuid;
    }

    public Date getEncounterDateTime() {
        return encounterDateTime;
    }

    public void setEncounterDateTime(Date encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }

    public Date getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(Date visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    public Set<EncounterTransaction.Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<EncounterTransaction.Provider> providers) {
        this.providers = providers;
    }


    public void addProvider(EncounterTransaction.Provider provider) {
        this.providers.add(provider);
    }
}
