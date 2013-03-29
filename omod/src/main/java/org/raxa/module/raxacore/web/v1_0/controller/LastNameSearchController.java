package org.raxa.module.raxacore.web.v1_0.controller;

import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.dao.NameListDao;
import org.raxa.module.raxacore.model.NameList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/v1/raxacore/lastname")
public class LastNameSearchController extends BaseRestController {
	
	private NameListDao namesDao;
	
	@Autowired
	public LastNameSearchController(NameListDao namesDao) {
		this.namesDao = namesDao;
	}
	
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@WSDoc("Save New Patient")
	@ResponseBody
	public NameList searchFor(@RequestParam String q) {
		return namesDao.getLastNames(q);
	}
}
