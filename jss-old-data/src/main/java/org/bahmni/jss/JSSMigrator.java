package org.bahmni.jss;

import org.bahmni.datamigration.Migrator;
import org.bahmni.datamigration.OpenMRSRESTConnection;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;
import org.bahmni.jss.registration.AllLookupValues;
import org.bahmni.jss.registration.AllRegistrations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class JSSMigrator {
    private final Migrator migrator;
    private final HashMap<String, AllLookupValues> lookupValuesMap;
    private String csvLocation;

    private static OpenMRSRESTConnection QA = new OpenMRSRESTConnection("172.18.2.1", "admin", "P@ssw0rd");
    private static OpenMRSRESTConnection Localhost = new OpenMRSRESTConnection("localhost", "admin", "Admin123");

    public static void main(String[] args) throws URISyntaxException, IOException {
        Localhost.getRestApiUrl();
        String csvLocation = "/Users/Vsingh/Projects/bhamni";
        JSSMigrator jssMigrator = new JSSMigrator(csvLocation, "LU_Caste.csv", "LU_District.csv", "LU_State.csv", "LU_Class.csv", QA);

        jssMigrator.migratePatient("RegistrationMaster.csv");
    }

    public JSSMigrator(String csvLocation, String casteFileName, String districtFileName, String stateFileName, String classFileName,
                       OpenMRSRESTConnection openMRSRESTConnection) throws IOException,
            URISyntaxException {
        this.csvLocation = csvLocation;
        AllLookupValues allCastes = new AllLookupValues(csvLocation, casteFileName);
        AllLookupValues allDistricts = new AllLookupValues(csvLocation, districtFileName);
        AllLookupValues allStates = new AllLookupValues(csvLocation, stateFileName);
        AllLookupValues allClasses = new AllLookupValues(csvLocation, classFileName);
        lookupValuesMap = new HashMap<String, AllLookupValues>();
        lookupValuesMap.put("Castes", allCastes);
        lookupValuesMap.put("Districts", allDistricts);
        lookupValuesMap.put("States", allStates);
        lookupValuesMap.put("Classes", allClasses);

        migrator = new Migrator(openMRSRESTConnection);
    }

    public void migratePatient(String csvFileName) throws IOException {
        AllPatientAttributeTypes allPatientAttributeTypes = migrator.getAllPatientAttributeTypes();
        AllRegistrations allRegistrations = new AllRegistrations(csvLocation, csvFileName, allPatientAttributeTypes, lookupValuesMap);
        try {
            migrator.migratePatient(allRegistrations);
        } finally {
            allRegistrations.done();
        }
    }
}