package org.bahmni.datamigration.session;

import java.util.HashMap;
import java.util.Map;

public class AllPatientAttributeTypes {
    private Map<String, String> personAttributeTypes = new HashMap<String, String>();

    public void addPersonAttributeType(String name, String uuid) {
        personAttributeTypes.put(name, uuid);
    }

    public String getAttributeUUID(String name) {
        return personAttributeTypes.get(name);
    }
}