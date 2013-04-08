package org.raxa.module.raxacore.web.v1_0.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.raxa.module.raxacore.dao.PersonNameDao;
import org.raxa.module.raxacore.model.ResultList;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PersonNameSearchControllerTest {
	
	@Mock
	PersonNameDao lastNameList;
	
	@Before
	public void setup() {
		initMocks(this);
	}
	
	@Test
	public void shouldCallDaoToSearchForPatientLastNames() {
		String query = "family";
		String key = "familyName";
		List<String> requiredResult = Arrays.asList("familyName1", "familyName2", "familyName3");
		when(lastNameList.getUnique(key, query)).thenReturn(new ResultList(requiredResult));
		PersonNameSearchController controller = new PersonNameSearchController(lastNameList);
		
		ResultList resultList = controller.searchFor(query, key);
		
		verify(lastNameList).getUnique(key, query);
		assertEquals(requiredResult.size(), resultList.size());
		for (String name : requiredResult) {
			assertTrue(resultList.getResults().contains(name));
		}
	}
}
