package org.bahmni.datamigration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookupValues {
    private Map<Integer, String> castes = new HashMap<Integer, String>();
    public LookupValues(List<String[]> casteRows) {
        for (String[] casteRow : casteRows)
            castes.put(Integer.parseInt(casteRow[0].trim()), casteRow[1]);
    }

    public String getValue(int id) {
        return castes.get(id);
    }
}