package org.bahmni.module.referencedatafeedclient.dao;

import org.bahmni.module.referencedatafeedclient.domain.TestUnitOfMeasure;

public interface BahmniTestUnitsDao {
    public void updateUnitsForTests(String newUnit, String oldUnit);
}
