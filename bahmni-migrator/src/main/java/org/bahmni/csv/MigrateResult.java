package org.bahmni.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MigrateResult<T extends CSVEntity> {
    private final List<String[]> migrationErrorRows = new ArrayList<>();
    private final List<String[]> validationErrorRows = new ArrayList<>();

    private String[] headerRow;
    private boolean validationFailed;
    private boolean migrationFailed;

    public void addHeaderRow(String[] headerRow) {
        this.headerRow = headerRow;
    }

    public void saveValidationErrors(CSVFile<T> fileLocation) throws IOException {
        saveErrors(fileLocation, validationErrorRows);
    }

    public void saveMigrationErrors(CSVFile<T> fileLocation) throws IOException {
        saveErrors(fileLocation, migrationErrorRows);
    }

    public void saveErrors(CSVFile<T> fileLocation,  List<String[]> recordToWrite) throws IOException {
        fileLocation.writeRecords(headerRow, recordToWrite);
    }

    public void addError(RowResult<T> rowResult, Stage stage) {
        if (stage == Stage.VALIDATION) {
            validationFailed = true;
            validationErrorRows.add(rowResult.getRowWithErrorColumn());
        } else {
            migrationFailed = true;
            migrationErrorRows.add(rowResult.getRowWithErrorColumn());
        }
    }

    public boolean isValidationSuccessful() {
        return !validationFailed;
    }

    public boolean isMigrationSuccessful() {
        return !validationFailed && !migrationFailed;
    }

    public int numberOfFailedValidationRecords() {
        return validationErrorRows.size();
    }

    public int numberOfFailedMigrationRecords() {
        return migrationErrorRows.size();
    }
}
