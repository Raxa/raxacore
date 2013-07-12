package org.bahmni.csv;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVFile<T extends CSVEntity> {
    public static final char SEPARATOR = ',';

    private File fileToRead;
    private Class<T> entityClass;

    private CSVReader csvReader;

    private CSVRow tempCSVRow;
    private String[] headerNames;

    public CSVFile(File fileToRead, Class<T> entityClass) {
        this.fileToRead = fileToRead;
        this.entityClass = entityClass;
    }

    public CSVEntity readEntity() throws IOException, InstantiationException, IllegalAccessException {
        if (csvReader == null)
            throw new RuntimeException("Please open the CSVFile before reading it");
        String[] aRow = csvReader.readNext();
        return tempCSVRow.getEntity(aRow);
    }

    public void close() {
        try {
            csvReader.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not close file. " + e.getMessage());
        }
    }

    public void open() throws IOException {
        csvReader = new CSVReader(new FileReader(fileToRead), SEPARATOR, '"', '\0');
        tempCSVRow = new CSVRow<T>(getHeaderColumn(), entityClass);
    }

    private CSVColumns getHeaderColumn() throws IOException {
        headerNames = csvReader.readNext();
        return new CSVColumns(headerNames);
    }

    public String[] getHeaderRow() {
        return headerNames;
    }
}
