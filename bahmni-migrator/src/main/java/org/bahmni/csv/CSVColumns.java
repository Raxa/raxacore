package org.bahmni.csv;

import org.bahmni.csv.exception.MigrationException;

import java.lang.reflect.Field;

class CSVColumns<T extends CSVEntity> {
    private final String[] headerNames;

    public CSVColumns(String[] headerNames) {
        this.headerNames = headerNames;
    }

    public void setValue(T entity, Field field, String[] aRow) throws IllegalAccessException {
        CSVHeader headerAnnotation = field.getAnnotation(CSVHeader.class);
        if (headerAnnotation == null)
            return;

        String headerValueInClass = headerAnnotation.name();
        field.setAccessible(true);
        field.set(entity, aRow[getPosition(headerValueInClass)]);
    }

    private int getPosition(String headerValueInClass) {
        for (int i = 0; i < headerNames.length; i++) {
            String headerName = headerNames[i];
            if (headerName.equalsIgnoreCase(headerValueInClass))
                return i;
        }
        throw new MigrationException("No Column found in the csv file. " + headerValueInClass);
    }
}
