package org.bahmni.jss;

import org.bahmni.address.AddressQueryExecutor;
import org.bahmni.address.sanitiser.AddressHierarchy;
import org.bahmni.address.sanitiser.AddressSanitiser;
import org.bahmni.address.sanitiser.LavensteinsDistance;
import org.bahmni.datamigration.Migrator;
import org.bahmni.datamigration.OpenMRSRESTConnection;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;
import org.bahmni.jss.registration.AllLookupValues;
import org.bahmni.jss.registration.AllRegistrations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class JSSMigrator {
    private final Migrator migrator;
    private final HashMap<String, AllLookupValues> lookupValuesMap;
    private String csvLocation;
    private AddressSanitiser addressSanitiser;

    private static OpenMRSRESTConnection QA = new OpenMRSRESTConnection("172.18.2.1", "admin", "P@ssw0rd");
    private static OpenMRSRESTConnection Localhost = new OpenMRSRESTConnection("localhost", "admin", "Hello123");

    public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException, SQLException {
        Localhost.getRestApiUrl();
        String csvLocation = "/Users/arathyja/bhamni/csv";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/openmrs","root", "arathy");

        try {
            AddressSanitiser addressSanitiser = new AddressSanitiser(new LavensteinsDistance(), new AddressHierarchy(new AddressQueryExecutor(connection)));
            JSSMigrator jssMigrator = new JSSMigrator(csvLocation, "LU_Caste.csv", "LU_District.csv", "LU_State.csv", "LU_Class.csv", "LU_Tahsil.csv", Localhost, addressSanitiser);
            jssMigrator.migratePatient("RegistrationMaster.csv");
        } finally {
            connection.close();
        }
    }

    public JSSMigrator(String csvLocation, String casteFileName, String districtFileName, String stateFileName, String classFileName, String tahsilFileName,
                       OpenMRSRESTConnection openMRSRESTConnection, AddressSanitiser addressSanitiser) throws IOException,
            URISyntaxException {
        this.csvLocation = csvLocation;
        this.addressSanitiser = addressSanitiser;
        AllLookupValues allCastes = new AllLookupValues(csvLocation, casteFileName);
        AllLookupValues allDistricts = new AllLookupValues(csvLocation, districtFileName);
        AllLookupValues allStates = new AllLookupValues(csvLocation, stateFileName);
        AllLookupValues allClasses = new AllLookupValues(csvLocation, classFileName);
        AllLookupValues allTahsils = new AllLookupValues(csvLocation, tahsilFileName);
        lookupValuesMap = new HashMap<String, AllLookupValues>();
        lookupValuesMap.put("Castes", allCastes);
        lookupValuesMap.put("Districts", allDistricts);
        lookupValuesMap.put("States", allStates);
        lookupValuesMap.put("Classes", allClasses);
        lookupValuesMap.put("Tahsils", allTahsils);

        migrator = new Migrator(openMRSRESTConnection);
    }

    public void migratePatient(String csvFileName) throws IOException {
        AllPatientAttributeTypes allPatientAttributeTypes = migrator.getAllPatientAttributeTypes();
        AllRegistrations allRegistrations = new AllRegistrations(csvLocation, csvFileName, allPatientAttributeTypes, lookupValuesMap, addressSanitiser);
        try {
            migrator.migratePatient(allRegistrations);
        } finally {
            allRegistrations.done();
        }
    }
}