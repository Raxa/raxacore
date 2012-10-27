package org.raxa.module.raxacore.db.hibernate;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.db.DrugGroupDAO;

public class HibernateDrugGroupDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATA_PATH = "org/raxa/module/raxacore/include/";
	
	private static final String MODULE_TEST_DATA_XML = TEST_DATA_PATH + "moduleTestData.xml";
	
	private DrugGroupDAO dao = null;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(MODULE_TEST_DATA_XML);
		dao = (HibernateDrugGroupDAO) applicationContext
		        .getBean("org.raxa.module.raxacore.db.hibernate.HibernateDrugGroupDAO");
	}
	
	@Test
	public void testSaveDrugGroup() {
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setName("TestDrugGroup3");
		drugGroup.setDescription("Third Test Drug Group");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465c-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		dao.saveDrugGroup(drugGroup);
		List<DrugGroup> result = dao.getDrugGroupByName("TestDrugGroup3");
		String name = result.get(0).getName();
		assertEquals(name, "TestDrugGroup3");
	}
	
	@Test
	public void testDeleteDrugGroup() {
		DrugGroup drugGroup = new DrugGroup();
		drugGroup.setId(2);
		drugGroup.setName("TestDrugGroup2");
		drugGroup.setDescription("Second Test Drug Group");
		drugGroup.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugGroup.setDateCreated(new java.util.Date());
		drugGroup.setUuid("68547121-1b70-465e-99ee-c9dfd95e7d30");
		drugGroup.setRetired(Boolean.FALSE);
		Set<Drug> drugs = new HashSet<Drug>();
		Drug drug1 = new Drug();
		Drug drug2 = new Drug();
		drug1.setId(1);
		drug1.setConcept(new Concept(792));
		drug1.setDateCreated(new Date());
		drug1.setCreator(Context.getUserContext().getAuthenticatedUser());
		drug2.setId(2);
		drug2.setConcept(new Concept(792));
		drug2.setDateCreated(new Date());
		drug2.setCreator(Context.getUserContext().getAuthenticatedUser());
		drugs.add(drug1);
		drugs.add(drug2);
		drugGroup.setDrugs(drugs);
		dao.deleteDrugGroup(drugGroup);
		DrugGroup result = dao.getDrugGroup(2);
		assertEquals(null, result);
	}
	
	@Test
	public void testGetDrugGroup() {
		Integer drugGroupId = 1;
		DrugGroup result = dao.getDrugGroup(drugGroupId);
		String name = result.getName();
		assertEquals("TestDrugGroup1", name);
		
		Set<Drug> resDrugs = result.getDrugs();
		Iterator<Drug> itr = resDrugs.iterator();
		Drug drug3 = itr.next();
		Integer drugId = 11;
		assertEquals(drug3.getId(), drugId);
	}
	
	@Test
	public void testGetDrugGroupByUuid() {
		String uuid = "68547121-1b70-465e-99ee-c9dfd95e7d30";
		String result = dao.getDrugGroupByUuid(uuid).getName();
		assertEquals("TestDrugGroup2", result);
	}
	
	@Test
	public void testGetDrugGroupByName() {
		String name = "TestDrugGroup1";
		String result = dao.getDrugGroupByName(name).get(0).getName();
		assertEquals(name, result);
	}
	
	@Test
	public void testUpdateDrugGroup() {
		String nameSet = "NewDrugGroupName";
		String nameRetrieved;
		DrugGroup drugGroup = dao.getDrugGroup(1);
		drugGroup.setName(nameSet);
		Set<Drug> drugs = new HashSet<Drug>();
		Drug drug1 = new Drug();
		Drug drug2 = new Drug();
		drug1.setId(1);
		drug1.setConcept(new Concept(792));
		drug2.setId(2);
		drug2.setConcept(new Concept(792));
		drugs.add(drug1);
		drugs.add(drug2);
		drugGroup.setDrugs(drugs);
		dao.updateDrugGroup(drugGroup);
		nameRetrieved = dao.getDrugGroup(1).getName();
		Set<Drug> resDrugs = dao.getDrugGroup(1).getDrugs();
		assertEquals(nameSet, nameRetrieved);
		assertEquals(resDrugs.contains(drug1), true);
		assertEquals(resDrugs.contains(drug2), true);
	}
}
