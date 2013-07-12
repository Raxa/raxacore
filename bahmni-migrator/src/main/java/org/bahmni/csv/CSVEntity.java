package org.bahmni.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// All fields have to be String
public abstract class CSVEntity {
    private List<String> originalRow = new ArrayList<String>();

    public String[] addErrorColumn(String errorMessage) {
        originalRow.add(errorMessage);
        return originalRow.toArray(new String[]{});
    }

    public void originalRow(String[] aRow) {
        List<String> originalRow = new ArrayList<String>(Arrays.asList(aRow));
        this.originalRow = originalRow;
    }

    public List<String> getOriginalRow() {
        return originalRow;
    }
}
