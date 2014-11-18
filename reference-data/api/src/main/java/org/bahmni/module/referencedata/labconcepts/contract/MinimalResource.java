package org.bahmni.module.referencedata.labconcepts.contract;

public class MinimalResource {
    private String name;
    private String uuid;

    public MinimalResource() {
    }

    public MinimalResource(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
