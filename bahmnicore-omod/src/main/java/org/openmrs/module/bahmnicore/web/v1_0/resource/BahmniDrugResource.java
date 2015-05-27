package org.openmrs.module.bahmnicore.web.v1_0.resource;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.DrugResource1_10;

@Resource(name = RestConstants.VERSION_1 + "/drug", supportedClass = org.openmrs.Drug.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*"}, order = 1)
public class BahmniDrugResource extends DrugResource1_10 {

    public BahmniDrugResource() {
        allowedMissingProperties.add("names");
        allowedMissingProperties.add("displayString");
    }

}
