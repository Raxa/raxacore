package org.bahmni.csv;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MigrateResult<T extends CSVEntity> {
    private List<String[]> errorRows = new ArrayList<String[]>();
    private List<String[]> validationRows = new ArrayList<String[]>();

    private String[] headerRow;
    private boolean validationFailed;
    private String fileName;

    public MigrateResult(String fileName) {
        this.fileName = fileName;
    }

    public void addHeaderRow(String[] headerRow) {
        this.headerRow = headerRow;
    }

    public void saveErrors(String fileLocation) throws IOException {
        if (!isValidationSuccessful()) {
            saveErrors(fileLocation, validationRows);
        }
        if (!isMigrationSuccessful()) {
            saveErrors(fileLocation, errorRows);
        }
    }

    public void saveErrors(String fileLocation,  List<String[]> rowsToWrite) throws IOException {
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(new File(fileLocation, errorFileName(fileName))));
            csvWriter.writeNext(headerRow);
            for (String[] rowToWrite : rowsToWrite) {
                csvWriter.writeNext(rowToWrite);
            }
        } finally {
            if (csvWriter != null) csvWriter.close();
        }
    }

    private String errorFileName(String fileName) {
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String fileNameAddition = validationFailed ? ".val.err" : ".err";
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        return fileNameWithoutExtension + fileNameAddition + fileExtension;
    }

    public void addMigrationError(MigrateRowResult<T> rowMigrateResult) {
        errorRows.add(rowMigrateResult.getRowWithErrorColumn());
    }

    public boolean isMigrationSuccessful() {
        return errorRows.isEmpty();
    }

    public void addValidationError(CSVEntity csvEntity, ValidateRowResult<T> validateRowResult) {
        if (validateRowResult.isSuccessful()) {
            validationRows.add(csvEntity.getOriginalRow().toArray(new String[] {}));
            return;
        }
        validationFailed = true;
        validationRows.add(csvEntity.addErrorColumn(validateRowResult.getErrorMessage()));
    }

    public boolean isValidationSuccessful() {
        return !validationFailed;
    }
}
