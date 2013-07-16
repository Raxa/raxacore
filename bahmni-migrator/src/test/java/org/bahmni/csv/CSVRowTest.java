package org.bahmni.csv;

import junit.framework.Assert;
import org.junit.Test;

public class CSVRowTest {
    @Test
    public void parse_a_row() throws InstantiationException, IllegalAccessException {
        String[] headerRows = new String[]{"id", "name"};
        String[] aRow = {"1", "bahmniUser"};
        CSVRow<DummyCSVEntity> entityCSVRow = new CSVRow<>(new CSVColumns(headerRows), DummyCSVEntity.class);
        DummyCSVEntity aDummyEntity = entityCSVRow.getEntity(aRow);
        Assert.assertEquals("bahmniUser", aDummyEntity.name);
        Assert.assertEquals("1", aDummyEntity.id);
    }

    @Test
    public void parse_a_row_ignoring_casing() throws InstantiationException, IllegalAccessException {
        String[] headerRows = new String[]{"Id", "NAME"};
        String[] aRow = {"1", "bahmniUser"};
        CSVRow<DummyCSVEntity> entityCSVRow = new CSVRow<>(new CSVColumns(headerRows), DummyCSVEntity.class);
        DummyCSVEntity aDummyEntity = entityCSVRow.getEntity(aRow);
        Assert.assertEquals("bahmniUser", aDummyEntity.name);
        Assert.assertEquals("1", aDummyEntity.id);
    }
}

