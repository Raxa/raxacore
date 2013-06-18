package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.model.BahmniPersonAttributeType;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class PersonAttributeTypeDaoImplTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PersonAttributeTypeDaoImpl personAttributeTypeDao;

    @Test
    public void shouldRetrievePersonAttributeTypeList() throws Exception {
        assertEquals(0, personAttributeTypeDao.getAll().size());
        executeDataSet("apiTestData.xml");
        List<BahmniPersonAttributeType> all = personAttributeTypeDao.getAll();
        assertEquals(1, all.size());
    }

}
