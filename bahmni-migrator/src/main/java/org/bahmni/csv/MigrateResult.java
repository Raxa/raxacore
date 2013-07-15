package org.bahmni.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MigrateResult<T extends CSVEntity> {
    private final List<String[]> errorRows = new ArrayList<String[]>();
    private final List<String[]> validationRows = new ArrayList<String[]>();

    private String[] headerRow;
    private boolean validationFailed;
    private boolean migrationFailed;

    public void addHeaderRow(String[] headerRow) {
        this.headerRow = headerRow;
    }

    public void saveValidationErrors(CSVFile<T> fileLocation) throws IOException {
        saveErrors(fileLocation, validationRows);
    }

    public void saveMigrationErrors(CSVFile<T> fileLocation) throws IOException {
        saveErrors(fileLocation, errorRows);
    }

    public void saveErrors(CSVFile<T> fileLocation,  List<String[]> recordToWrite) throws IOException {
        fileLocation.writeRecords(headerRow, recordToWrite);
    }

    public void addMigrationError(MigrateRowResult<T> rowMigrateResult) {
        migrationFailed = true;
        errorRows.add(rowMigrateResult.getRowWithErrorColumn());
    }

    public void addValidationError(ValidateRowResult<T> validateRowResult) {
        validationFailed = true;
        validationRows.add(validateRowResult.getRowWithErrorColumn());
    }

    public boolean isValidationSuccessful() {
        return !validationFailed;
    }

    public boolean isMigrationSuccessful() {
        return !validationFailed && !migrationFailed;
    }

    public int numberOfFailedValidationRecords() {
        return validationRows.size();
    }

    public int numberOfFailedMigrationRecords() {
        return errorRows.size();
    }
}
