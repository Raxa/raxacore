package org.bahmni.jss;

import org.bahmni.datamigration.Migrator;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;
import org.bahmni.jss.registration.AllLookupValues;
import org.bahmni.jss.registration.AllRegistrations;

import java.io.IOException;
import java.net.URISyntaxException;

public class JSSMigrator {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String openMRSAPIUrl = "http://172.18.2.1:8080/openmrs/ws/rest/v1/";
        String csvLocation = "/Users/Vsingh/Projects/bhamni";
        AllLookupValues allCastes = new AllLookupValues(csvLocation, "LU_Caste.csv");
        AllLookupValues allEducations = new AllLookupValues(csvLocation, "LU_Education.csv");
        AllLookupValues allOccupations = new AllLookupValues(csvLocation, "LU_Occupation.csv");
        AllLookupValues allClasses = new AllLookupValues(csvLocation, "LU_Class.csv");
        Migrator migrator = new Migrator(openMRSAPIUrl, "admin", "P@ssw0rd");
        AllPatientAttributeTypes allPatientAttributeTypes = migrator.getAllPatientAttributeTypes();
        AllRegistrations allRegistrations = new AllRegistrations(csvLocation, "RegistrationMaster.csv", allPatientAttributeTypes, allCastes);
        migrator.migratePatient(allRegistrations);
    }
}