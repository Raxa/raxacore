package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PatientDao;
import org.bahmni.module.bahmnicore.dao.impl.PatientDaoImpl;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrder;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BahmniFeedDrugOrderServiceImplTest {
    public static final String TEST_VISIT_TYPE = "TEST VISIT TYPE";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

}
