package org.bahmni.module.elisatomfeedclient.api.domain;

import org.openmrs.Encounter;
import org.openmrs.Provider;

public class OpenElisAccessionNote {
    private String note;
    private String providerUuid;

    public OpenElisAccessionNote() {
    }

    public OpenElisAccessionNote(String note, String providerUuid) {
        this.note = note;
        this.providerUuid = providerUuid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProviderUuid() {
        return providerUuid;
    }

    public void setProviderUuid(String providerUuid) {
        this.providerUuid = providerUuid;
    }

    public boolean isProviderInEncounter(Encounter encounter){
        return encounter.getEncounterProviders().iterator().next().getProvider().getUuid().equals(providerUuid);
    }

    public boolean matchesProvider(Provider provider){
        return  providerUuid.equals(provider.getUuid());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpenElisAccessionNote that = (OpenElisAccessionNote) o;

        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (providerUuid != null ? !providerUuid.equals(that.providerUuid) : that.providerUuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = note != null ? note.hashCode() : 0;
        result = 31 * result + (providerUuid != null ? providerUuid.hashCode() : 0);
        return result;
    }
}
