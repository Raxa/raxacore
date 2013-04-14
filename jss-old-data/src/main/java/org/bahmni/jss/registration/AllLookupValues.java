package org.bahmni.jss.registration;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllLookupValues implements LookupValueProvider {
    private static Logger logger = Logger.getLogger(AllLookupValues.class);
    private Map<Integer, String> map = new HashMap<Integer, String>();

    protected AllLookupValues() {
    }

    public AllLookupValues(String csvLocation, String fileName) throws IOException {
        File file = new File(csvLocation, fileName);
        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file), ',');
            reader.readNext(); //ignore header
            List<String[]> rows = reader.readAll();
            logger.info(String.format("Found %d lookupValues", rows.size()));
            for (String[] row : rows)
                map.put(Integer.parseInt(row[0].trim()), row[1]);
        } finally {
            if (reader != null) reader.close();
        }
    }

    public String getLookUpValue(String key) {
        return map.get(Integer.parseInt(key.trim()));
    }
}