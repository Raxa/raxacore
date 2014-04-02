package org.bahmni.module.bahmnicore.model;

public class NameSearchParameter {
    private final String[] nameParts;

    public NameSearchParameter(String[] nameParts) {
        this.nameParts = nameParts;
    }

    public static NameSearchParameter create(String value) {
        if(value == null || value == ""){
            return new NameSearchParameter(new String[0]);
        }
        String[] splitName = value.split(" ");
        for(int i=0;i<splitName.length ; i++){
            splitName[i] = "%" + splitName[i] + "%";
        }
        return new NameSearchParameter(splitName);
    }

    public String[] getNameParts() {
        return nameParts;
    }

    public boolean isEmpty() {
        return nameParts.length == 0;
    }
}
