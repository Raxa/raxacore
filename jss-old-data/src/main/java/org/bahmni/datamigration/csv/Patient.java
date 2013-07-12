package org.bahmni.datamigration.csv;


import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.CSVHeader;

public class Patient extends CSVEntity {
    @CSVHeader(name="REG_NO")
    private String registrationNumber;
    @CSVHeader(name="REG_DATE")
    private String registrationDate;
    @CSVHeader(name="FNAME")
    private String firstName;
    @CSVHeader(name="LNAME")
    private String lastName;
    @CSVHeader(name="FHNAME")
    private String fathersName;
    @CSVHeader(name="P_SEX")
    private String sex;
    @CSVHeader(name="P_DOB")
    private String dob;
    @CSVHeader(name="P_AGE")
    private String age;
    @CSVHeader(name="P_HEIGHT")
    private String height;
    @CSVHeader(name="P_WEIGHT")
    private String weight;
    @CSVHeader(name="VILLAGE")
    private String village;
    @CSVHeader(name="CITY")
    private String city;
    @CSVHeader(name="P_POST")
    private String postOffice;
    @CSVHeader(name="EDUCATION")
    private String education;
    @CSVHeader(name="OCCUPATION")
    private String occupation;
    @CSVHeader(name="P_MEMBER")
    private String primaryMember;
    @CSVHeader(name="P_TB")
    private String hasTB;
    @CSVHeader(name="BALANCE_AMT")
    private String balanceAmount;
    @CSVHeader(name="Remark")
    private String remark;
    @CSVHeader(name="FNameID")
    private String fNameId;
    @CSVHeader(name="CasteID")
    private String casteId;
    @CSVHeader(name="FHNameID")
    private String fhNameId;
    @CSVHeader(name="EducationID")
    private String educationId;
    @CSVHeader(name="OccupationID")
    private String occupationId;
    @CSVHeader(name="VillageID")
    private String villageId;
    @CSVHeader(name="TahsilID")
    private String tahsilId;
    @CSVHeader(name="DistrictID")
    private String districtId;
    @CSVHeader(name="TahsilID2")
    private String tahsilId2;
    @CSVHeader(name="VillageID2")
    private String villageId2;
    @CSVHeader(name="Neighborhood")
    private String neighbourhood;
    @CSVHeader(name="GramPanchID")
    private String gramPanchayatId;
    @CSVHeader(name="LNameID")
    private String lastNameId;
    @CSVHeader(name="ClassID")
    private String classId;
    @CSVHeader(name="memberVillageID")
    private String memberVillageId;
    @CSVHeader(name="GramPanch")
    private String gramPanch;
    @CSVHeader(name="Tahsil")
    private String tahsil;

    public String getDistrictId() {
        return districtId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFathersName() {
        return fathersName;
    }

    public String getSex() {
        return sex;
    }

    public String getDob() {
        return dob;
    }

    public String getAge() {
        return age;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getVillage() {
        return village;
    }

    public String getCity() {
        return city;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public String getEducation() {
        return education;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getPrimaryMember() {
        return primaryMember;
    }

    public String getHasTB() {
        return hasTB;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public String getRemark() {
        return remark;
    }

    public String getfNameId() {
        return fNameId;
    }

    public String getCasteId() {
        return casteId;
    }

    public String getFhNameId() {
        return fhNameId;
    }

    public String getEducationId() {
        return educationId;
    }

    public String getOccupationId() {
        return occupationId;
    }

    public String getVillageId() {
        return villageId;
    }

    public String getTahsilId() {
        return tahsilId;
    }

    public String getTahsilId2() {
        return tahsilId2;
    }

    public String getVillageId2() {
        return villageId2;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public String getGramPanchayatId() {
        return gramPanchayatId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLastNameId() {
        return lastNameId;
    }

    public String getClassId() {
        return classId;
    }

    public String getMemberVillageId() {
        return memberVillageId;
    }

    public String getGramPanch() {
        return gramPanch;
    }

    public String getTahsil() {
        return tahsil;
    }
}
