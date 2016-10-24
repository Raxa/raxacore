package org.bahmni.module.bahmnicore.obs.handler;

import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.obs.ComplexData;
import org.openmrs.obs.ComplexObsHandler;
import org.openmrs.obs.handler.AbstractHandler;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlHandler extends AbstractHandler implements ComplexObsHandler {

    @Override
    public Obs saveObs(Obs obs) throws APIException {
        //doing this just to satisfy openmrs obsService.save - it will fail if complex obs does not complex data
        obs.setComplexData(new ComplexData(obs.getValueComplex(), null));
        return obs;
    }

}
