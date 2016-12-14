package org.bahmni.module.bahmnicore.obs.handler;

import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.obs.ComplexObsHandler;
import org.openmrs.obs.handler.AbstractHandler;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlHandler extends AbstractHandler implements ComplexObsHandler {

    @Override
    public Obs saveObs(Obs obs) throws APIException {
        return obs;
    }
}
