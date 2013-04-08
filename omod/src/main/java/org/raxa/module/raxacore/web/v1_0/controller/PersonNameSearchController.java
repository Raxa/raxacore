package org.raxa.module.raxacore.web.v1_0.controller;

import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.dao.PersonNameDao;
import org.raxa.module.raxacore.model.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/rest/v1/raxacore/unique/personname")
public class PersonNameSearchController extends BaseRestController {
	
	private PersonNameDao namesDao;
	
	@Autowired
	public PersonNameSearchController(PersonNameDao namesDao) {
		this.namesDao = namesDao;
	}
	
	@RequestMapping(method = RequestMethod.GET, params = { "q", "key" })
	@WSDoc("Returns unique patient attributes for the given key that match the query term")
	public ResultList searchFor(@RequestParam String q, @RequestParam String key) {
		return namesDao.getUnique(key, q);
	}
}
