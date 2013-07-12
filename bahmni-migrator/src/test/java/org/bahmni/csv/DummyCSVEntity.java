package org.bahmni.csv;

public class DummyCSVEntity extends CSVEntity {
    @CSVHeader(name = "id")
    private String id;
    @CSVHeader(name = "name")
    private String name;

    private Object something;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
