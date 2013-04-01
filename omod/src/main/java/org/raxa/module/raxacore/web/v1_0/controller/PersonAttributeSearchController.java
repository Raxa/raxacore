package org.raxa.module.raxacore.web.v1_0.controller;

import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.raxa.module.raxacore.dao.PersonAttributeDoa;
import org.raxa.module.raxacore.model.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/rest/v1/raxacore/personattribute/unique")
public class PersonAttributeSearchController {

    private PersonAttributeDoa personAttributeDoa;

    @Autowired
    public PersonAttributeSearchController(PersonAttributeDoa personAttributeDoa) {
        this.personAttributeDoa = personAttributeDoa;
    }

    @RequestMapping(method = RequestMethod.GET)
    @WSDoc("Get unique values for a person attribute")
    public ResultList search(@RequestParam(value = "key") String personAttribute,
            @RequestParam(value = "query") String query) {
        return personAttributeDoa.getUnique(personAttribute, query);
    }
}
