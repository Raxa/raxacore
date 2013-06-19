package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.model.ResultList;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class ActivePatientListDaoImplTest  extends BaseModuleContextSensitiveTest {

    @Autowired
    ActivePatientListDaoImpl activePatientListDao;

    @Test
    public void shouldGetListOfActivePatients() throws Exception {
        executeDataSet("apiTestData.xml");

        ResultList resultList = activePatientListDao.getUnique("Ganiyari");

        assertTrue(resultList.size() > 0);
    }

    @Test
    public void shouldNotReturnPatientsWhoAreNotActive() throws Exception {
        executeDataSet("apiTestData.xml");

        ResultList resultList = activePatientListDao.getUnique("Ganiyari");

        for(Object patientObject : resultList.getResults()){
            Object[] pObject = (Object[]) patientObject;
            String patientIdentifier = (String) pObject[2];
            assertFalse(patientIdentifier.equalsIgnoreCase("GAN200008"));
            assertFalse(patientIdentifier.equalsIgnoreCase("GAN200007"));
        }
    }

}
