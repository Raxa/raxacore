package org.bahmni.module.referencedata.labconcepts.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.MinimalResource;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

public class MinimalResourceMapper {
    public MinimalResource map(Concept concept) {
        return new MinimalResource(concept.getUuid(), concept.getName(Context.getLocale()).getName());
    }
}
