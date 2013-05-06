package org.bahmni.address.sanitiser;

public class PersonAddress {
    private String village;
    private String tehsil;
    private String district;
    private String state;

    public PersonAddress(String village, String tehsil, String district, String state) {
        this.village = village;
        this.tehsil = tehsil;
        this.district = district;
        this.state = state;
    }

    public String getVillage() {
        return village;
    }

    public String getTehsil() {
        return tehsil;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }
}
