package org.bahmni.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// All fields have to be String
public abstract class CSVEntity {
    private List<String> originalRow = new ArrayList<>();

    public String[] getRowWithErrorColumn(String errorMessage) {
        List<String> tempList = new ArrayList<>();
        tempList.addAll(originalRow);
        tempList.add(errorMessage);

        return tempList.toArray(new String[]{});
    }

    public void originalRow(String[] aRow) {
        List<String> originalRow = new ArrayList<>(Arrays.asList(aRow));
        this.originalRow = originalRow;
    }

    public List<String> getOriginalRow() {
        return originalRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSVEntity csvEntity = (CSVEntity) o;

        if (originalRow != null ? !originalRow.equals(csvEntity.originalRow) : csvEntity.originalRow != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return originalRow != null ? originalRow.hashCode() : 0;
    }
}
