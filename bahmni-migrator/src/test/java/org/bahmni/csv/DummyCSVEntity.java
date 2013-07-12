package org.bahmni.csv;

public class DummyCSVEntity extends CSVEntity {
    @CSVHeader(name = "id")
    private String id;
    @CSVHeader(name = "name")
    private String name;

    private Object something;

    public DummyCSVEntity() {
    }

    public DummyCSVEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
