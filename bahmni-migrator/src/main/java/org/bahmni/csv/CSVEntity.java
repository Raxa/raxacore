package org.bahmni.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// All fields have to be String
public abstract class CSVEntity {
    private List<String> originalRow = new ArrayList<>();

    public String[] getRowWithErrorColumn(String errorMessage) {
        if (!originalRow.contains(errorMessage))
            originalRow.add(errorMessage);

        return originalRow.toArray(new String[]{});
    }

    public void originalRow(String[] aRow) {
        List<String> originalRow = new ArrayList<>(Arrays.asList(aRow));
        this.originalRow = originalRow;
    }

    public List<String> getOriginalRow() {
        return originalRow;
    }
}
