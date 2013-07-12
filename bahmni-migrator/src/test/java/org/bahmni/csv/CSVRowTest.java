package org.bahmni.csv;

import junit.framework.Assert;
import org.junit.Test;

public class CSVRowTest {
    @Test
    public void parse_a_row() throws InstantiationException, IllegalAccessException {
        String[] headerRows = new String[]{"id", "name"};
        String[] aRow = {"1", "bahmniUser"};
        DummyCSVEntity aDummyEntity = new CSVRow<DummyCSVEntity>(new CSVColumns(headerRows), DummyCSVEntity.class).getEntity(aRow);
        Assert.assertEquals("bahmniUser", aDummyEntity.getName());
        Assert.assertEquals("1", aDummyEntity.getId());
    }

}

