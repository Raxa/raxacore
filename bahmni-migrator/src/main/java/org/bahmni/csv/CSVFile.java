package org.bahmni.csv;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.bahmni.csv.exception.MigrationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class CSVFile<T extends CSVEntity> {
    public static final char SEPARATOR = ',';

    private String fileName;
    private Class<T> entityClass;
    private String fileLocation;

    private CSVReader csvReader;

    private String[] headerNames;

    public CSVFile(String fileLocation, String fileName, Class<T> entityClass) {
        this.fileLocation = fileLocation;
        this.fileName = fileName;
        this.entityClass = entityClass;
    }

    public CSVEntity readEntity() throws IOException, InstantiationException, IllegalAccessException {
        if (csvReader == null)
            throw new MigrationException("Please open the CSVFile before reading it");
        String[] aRow = csvReader.readNext();
        CSVRow tempCSVRow = new CSVRow<>(getHeaderColumn(), entityClass);
        return tempCSVRow.getEntity(aRow);
    }

    public void open() throws IOException {
        File file = new File(fileLocation, fileName);
        if (!file.exists())
            throw new MigrationException("Input CSV file does not exist. File - " + file.getAbsolutePath());

        csvReader = new CSVReader(new FileReader(file), SEPARATOR, '"', '\0');
        headerNames = csvReader.readNext();
    }

    public void close() {
        try {
            if (csvReader != null) csvReader.close();
        } catch (IOException e) {
            throw new MigrationException("Could not close file. " + e.getMessage(), e);
        }
    }

    public void writeRecords(String[] headerRow, List<String[]> recordToWrite) throws IOException {
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(new File(fileLocation, fileName)));
            csvWriter.writeNext(headerRow);
            for (String[] rowToWrite : recordToWrite) {
                csvWriter.writeNext(rowToWrite);
            }
        } finally {
            if (csvWriter != null) csvWriter.close();
        }
    }

    private CSVColumns getHeaderColumn() throws IOException {
        return new CSVColumns(headerNames);
    }

    public String[] getHeaderRow() {
        return headerNames;
    }

    public String getAbsoluteFileName() {
        return fileLocation + "/" + fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
