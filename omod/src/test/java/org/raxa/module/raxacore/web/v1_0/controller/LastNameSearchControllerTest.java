package org.raxa.module.raxacore.web.v1_0.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.raxa.module.raxacore.dao.NameListDao;
import org.raxa.module.raxacore.model.NameList;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LastNameSearchControllerTest {
	
	@Mock
	NameListDao lastNameList;
	
	@Before
	public void setup() {
		initMocks(this);
	}
	
	@Test
	public void shouldCallDaoToSearchForPatientLastNames() {
		String query = "familyName";
		List<String> requiredResult = Arrays.asList("familyName1", "familyName2", "familyName3");
		when(lastNameList.getLastNames(query)).thenReturn(new NameList(requiredResult));
		LastNameSearchController controller = new LastNameSearchController(lastNameList);
		
		NameList nameList = controller.searchFor(query);
		
		verify(lastNameList).getLastNames(query);
		assertEquals(requiredResult.size(), nameList.size());
		for (String name : requiredResult) {
			assertTrue(nameList.getNames().contains(name));
		}
	}
}
