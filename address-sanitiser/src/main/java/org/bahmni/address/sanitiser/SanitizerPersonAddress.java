package org.bahmni.address.sanitiser;

public class SanitizerPersonAddress {
    private String village;
    private String tehsil;
    private String district;
    private String state;

    public SanitizerPersonAddress(String village, String tehsil, String district, String state) {
        this.village = village;
        this.tehsil = tehsil;
        this.district = district;
        this.state = state;
    }

    public SanitizerPersonAddress() {
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

    public void setVillage(String village) {
        this.village = village;
    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setState(String state) {
        this.state = state;
    }
}
