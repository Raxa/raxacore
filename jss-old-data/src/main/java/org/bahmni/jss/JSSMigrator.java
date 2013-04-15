package org.bahmni.jss;

import org.bahmni.datamigration.Migrator;
import org.bahmni.datamigration.OpenMRSRESTConnection;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;
import org.bahmni.jss.registration.AllLookupValues;
import org.bahmni.jss.registration.AllRegistrations;

import java.io.IOException;
import java.net.URISyntaxException;

public class JSSMigrator {
    private final Migrator migrator;
    private final AllLookupValues allCastes;
    private String csvLocation;

    private static OpenMRSRESTConnection QA = new OpenMRSRESTConnection("172.18.2.1", "admin", "P@ssw0rd");
    private static OpenMRSRESTConnection Localhost = new OpenMRSRESTConnection("localhost", "admin", "Admin123");

    public static void main(String[] args) throws URISyntaxException, IOException {
        Localhost.getRestApiUrl();
        String csvLocation = "/Users/Vsingh/Projects/bhamni";
        JSSMigrator jssMigrator = new JSSMigrator(csvLocation, "LU_Caste.csv", "LU_Education.csv", "LU_Occupation.csv", "LU_Class.csv", Localhost);

        jssMigrator.migratePatient("RegistrationMaster.csv");
    }

    public JSSMigrator(String csvLocation, String casteFileName, String educationFileName, String occupationFileName, String classFileName,
                       OpenMRSRESTConnection openMRSRESTConnection) throws IOException,
            URISyntaxException {
        this.csvLocation = csvLocation;
        allCastes = new AllLookupValues(csvLocation, casteFileName);
        AllLookupValues allEducations = new AllLookupValues(csvLocation, educationFileName);
        AllLookupValues allOccupations = new AllLookupValues(csvLocation, occupationFileName);
        AllLookupValues allClasses = new AllLookupValues(csvLocation, classFileName);

        migrator = new Migrator(openMRSRESTConnection);
    }

    public void migratePatient(String csvFileName) throws IOException {
        AllPatientAttributeTypes allPatientAttributeTypes = migrator.getAllPatientAttributeTypes();
        AllRegistrations allRegistrations = new AllRegistrations(csvLocation, csvFileName, allPatientAttributeTypes, allCastes);
        try {
            migrator.migratePatient(allRegistrations);
        } finally {
            allRegistrations.done();
        }
    }
}