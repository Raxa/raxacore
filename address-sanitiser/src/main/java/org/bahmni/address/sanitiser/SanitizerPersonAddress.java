package org.bahmni.address.sanitiser;

import org.apache.commons.lang.StringUtils;

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

    public boolean isEmpty(){
        return StringUtils.isBlank(village) &&
               StringUtils.isBlank(tehsil) &&
               StringUtils.isBlank(state) &&
               StringUtils.isBlank(district);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanitizerPersonAddress)) return false;

        SanitizerPersonAddress that = (SanitizerPersonAddress) o;

        if (district != null ? !StringUtils.equalsIgnoreCase(district, that.district)  : that.district != null) return false;
        if (state != null ? !StringUtils.equalsIgnoreCase(state, that.state) : that.state != null) return false;
        if (tehsil != null ? !StringUtils.equalsIgnoreCase(tehsil, that.tehsil) : that.tehsil != null) return false;
        if (village != null ? !StringUtils.equalsIgnoreCase(village, that.village) : that.village != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = village != null ? village.hashCode() : 0;
        result = 31 * result + (tehsil != null ? tehsil.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
