package org.bahmni.address;

public class AddressHierarchyEntry {
    private int parentId;
    private String name;

    public AddressHierarchyEntry(int parentId, String name) {
        this.parentId = parentId;
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }
}
