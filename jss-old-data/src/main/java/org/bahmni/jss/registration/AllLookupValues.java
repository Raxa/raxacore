package org.bahmni.jss.registration;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.bahmni.datamigration.LookupValues;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class AllLookupValues implements LookupValueProvider {
    private static Logger logger = Logger.getLogger(AllLookupValues.class);
    private final LookupValues lookupValues;

    public AllLookupValues(String csvLocation, String fileName) throws IOException {
        File file = new File(csvLocation, fileName);
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file), ',');
            reader.readNext(); //ignore header
            List<String[]> casteRows = reader.readAll();
            logger.info(String.format("Found %d lookupValues", casteRows.size()));
            lookupValues = new LookupValues(casteRows);
        } finally {
            if (reader != null) reader.close();
        }
    }

    public String getLookUpValue(String key) {
        return lookupValues.getValue(Integer.parseInt(key.trim()));
    }
}