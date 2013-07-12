package org.bahmni.csv;

import au.com.bytecode.opencsv.CSVReader;
import org.bahmni.csv.exception.MigrationException;

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
            throw new MigrationException("Please open the CSVFile before reading it");
        String[] aRow = csvReader.readNext();
        return tempCSVRow.getEntity(aRow);
    }

    public void close() {
        try {
            if (csvReader != null) csvReader.close();
        } catch (IOException e) {
            throw new MigrationException("Could not close file. " + e.getMessage(), e);
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

    public File getAbsoluteFile() {
        return fileToRead.getAbsoluteFile();
    }

    public String getFileName() {
        return fileToRead.getName();
    }
}
