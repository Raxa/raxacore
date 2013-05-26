package org.bahmni.jss;

import com.mysql.jdbc.Driver;
import org.apache.log4j.Logger;
import org.bahmni.address.AddressQueryExecutor;
import org.bahmni.address.sanitiser.AddressHierarchy;
import org.bahmni.address.sanitiser.AddressSanitiser;
import org.bahmni.address.sanitiser.LavensteinsDistance;
import org.bahmni.datamigration.Migrator;
import org.bahmni.datamigration.OpenMRSRESTConnection;
import org.bahmni.datamigration.session.AllPatientAttributeTypes;
import org.bahmni.datamigration.AllLookupValues;
import org.bahmni.jss.registration.AllRegistrations;

import java.io.File;
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
    private static Logger logger = Logger.getLogger(JSSMigrator.class);

    public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException, SQLException {
        if (args.length < 2) {
            logger.error(String.format("Usage %s CSV-File-Location RegistrationCSVFileName", JSSMigrator.class.getName()));
            logPropertyUsage("localhost", "root", "password", "admin", "test");
            System.exit(1);
        }

        String csvLocation = args[0];
        String registrationCSVFileName = args[1];
        logger.info(String.format("Using CSVFileLocation=%s; RegistrationFileName=%s", new File(csvLocation).getAbsolutePath(), registrationCSVFileName));

        String openMRSHostName = System.getProperty("openmrs.host.name", "localhost");
        String databaseUserId = System.getProperty("database.user.id", "root");
        String databasePassword = System.getProperty("database.user.password", "password");
        String openmrsUserId = System.getProperty("openmrs.user.id", "admin");
        String openmrsUserPassword = System.getProperty("openmrs.user.password", "test");
        logPropertyUsage(openMRSHostName, databaseUserId, databasePassword, openmrsUserId, openmrsUserPassword);

        OpenMRSRESTConnection openMRSRESTConnection = new OpenMRSRESTConnection(openMRSHostName, openmrsUserId, openmrsUserPassword);

        Class<Driver> variableToLoadDriver = Driver.class;
        String url = String.format("jdbc:mysql://%s:3306/openmrs", openMRSHostName);
        Connection connection = DriverManager.getConnection(url, databaseUserId, databasePassword);

        try {
            AddressSanitiser addressSanitiser = new AddressSanitiser(new LavensteinsDistance(), new AddressHierarchy(new AddressQueryExecutor(connection)));
            JSSMigrator jssMigrator = new JSSMigrator(csvLocation, "LU_Caste.csv", "LU_District.csv", "LU_State.csv",
                    "LU_Class.csv", "LU_Tahsil.csv", openMRSRESTConnection, addressSanitiser);
            jssMigrator.migratePatient(registrationCSVFileName);
        } finally {
            connection.close();
        }
    }

    private static void logPropertyUsage(String openMRSHostName, String databaseUserId, String databaseUserPassword, String openmrsUserId, String openmrsPassword) {
        logger.info(String.format("By default uses following properties: openmrs.host.name=%s; database.user.id=%s; database.user.password=%s; openmrs.user.id=%s; " +
                "openmrs.user.password=%s", openMRSHostName, databaseUserId, databaseUserPassword, openmrsUserId, openmrsPassword));
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