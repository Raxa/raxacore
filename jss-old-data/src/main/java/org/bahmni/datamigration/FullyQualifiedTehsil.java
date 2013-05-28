package org.bahmni.datamigration;

public class FullyQualifiedTehsil {
    private String tehsil;
    private String district;
    private String state;

    public FullyQualifiedTehsil(String tehsil, String district, String state) {
        this.tehsil = tehsil;
        this.district = district;
        this.state = state;
    }

    public FullyQualifiedTehsil() {
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

    public String getTehsil() {
        return tehsil;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullyQualifiedTehsil that = (FullyQualifiedTehsil) o;

        if (!district.equals(that.district)) return false;
        if (!state.equals(that.state)) return false;
        if (!tehsil.equals(that.tehsil)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tehsil.hashCode();
        result = 31 * result + district.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }
}