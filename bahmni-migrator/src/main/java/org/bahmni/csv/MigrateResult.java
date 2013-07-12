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

    public void addHeaderRow(String[] headerRow) {
        this.headerRow = headerRow;
    }

    public void saveErrors(String fileLocation, String fileName) throws IOException {
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(new File(fileLocation, fileName)));
            csvWriter.writeNext(headerRow);
            for (String[] errorRow : errorRows) {
                csvWriter.writeNext(errorRow);
            }
        } finally {
            if (csvWriter != null) csvWriter.close();
        }
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
