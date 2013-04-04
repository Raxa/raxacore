package org.raxa.module.raxacore.web.v1_0.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.raxa.module.raxacore.dao.PersonAttributeDao;
import org.raxa.module.raxacore.model.ResultList;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PersonAttributeSearchControllerTest {
	
	private PersonAttributeSearchController controller;
	
	@Mock
    PersonAttributeDao personAttributeDao;
	
	@Before
	public void init() {
		initMocks(this);
		controller = new PersonAttributeSearchController(personAttributeDao);
	}
	
	@Test
	public void shouldCallDaoToSearchForPatientAttributeValuesForCaste() {
		String query = "someCaste";
		String personAttribute = "caste";
		when(personAttributeDao.getUnique(personAttribute, query)).thenReturn(
		    new ResultList(Arrays.asList("blah1", "blah2", "blah3")));
		
		controller.search(personAttribute, query);
		
		verify(personAttributeDao).getUnique(personAttribute, query);
	}
	
}
