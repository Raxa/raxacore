package org.raxa.module.raxacore.web.v1_0.controller;

import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.dao.PersonAttributeDoa;
import org.raxa.module.raxacore.model.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/rest/v1/raxacore/unique/personattribute")
public class PersonAttributeSearchController extends BaseRestController {

	private PersonAttributeDoa personAttributeDoa;

	@Autowired
	public PersonAttributeSearchController(PersonAttributeDoa personAttributeDoa) {
		this.personAttributeDoa = personAttributeDoa;
	}

	@RequestMapping(method = RequestMethod.GET, params = { "q", "key" })
	@WSDoc("Get unique values for a person attribute")
	public ResultList search(@RequestParam String key, @RequestParam String q) {
		return personAttributeDoa.getUnique(key, q);
	}
}
