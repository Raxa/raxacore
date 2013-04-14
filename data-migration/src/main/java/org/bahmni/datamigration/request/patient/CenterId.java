package org.bahmni.datamigration.request.patient;

public class CenterId {
    private String name;

    public CenterId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}