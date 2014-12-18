package org.bahmni.module.bahmnicore.service.impl;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.contract.encounter.data.ConceptData;
import org.bahmni.module.bahmnicore.encounterModifier.EncounterModifier;
import org.bahmni.module.bahmnicore.encounterModifier.exception.CannotModifyEncounterException;
import org.bahmni.module.bahmnicore.service.BahmniEncounterModifierService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BahmniEncounterModifierServiceImpl implements BahmniEncounterModifierService {

    public static final String ENCOUNTER_MODIFIER_ALGORITHM_DIRECTORY = "/encounterModifier/";
    public static final String ENCOUNTER_MODIFIER_GROOVY_ALLOW_CACHING = "encounterModifier.groovy.allowCaching";
    protected Map<String, EncounterModifier> encounterModifiers = new HashMap<>();

    private static final Logger log = Logger.getLogger(BahmniEncounterModifierServiceImpl.class);

    @Override
    public BahmniEncounterTransaction getModifiedEncounter(BahmniEncounterTransaction bahmniEncounterTransaction, ConceptData conceptSetData) throws CannotModifyEncounterException, InstantiationException, IllegalAccessException, IOException {
        String encounterModifierClassName = conceptSetData.getName().replaceAll(" ", "").concat(".groovy");
        return modifyEncounter(bahmniEncounterTransaction, encounterModifierClassName, conceptSetData);
    }

    private BahmniEncounterTransaction modifyEncounter(BahmniEncounterTransaction bahmniEncounterTransaction, String encounterModifierClassName, ConceptData conceptSetData) throws IOException, IllegalAccessException, InstantiationException, CannotModifyEncounterException {
        if (StringUtils.isEmpty(conceptSetData.getName())) {
            return bahmniEncounterTransaction;
        }
        EncounterModifier encounterModifier = getEncounterModifierAlgorithm(encounterModifierClassName);
        log.debug("EncounterModifier : Using Algorithm in " + encounterModifier.getClass().getName());
        return encounterModifier.run(bahmniEncounterTransaction, conceptSetData);
    }

    private EncounterModifier getEncounterModifierAlgorithm(String encounterModifierClassName) throws IOException, InstantiationException, IllegalAccessException {
        if(isGroovyCachingAllowed()){
            EncounterModifier encounterModifier = encounterModifiers.get(encounterModifierClassName);
            if (encounterModifier == null) {
                encounterModifiers.put(encounterModifierClassName, loadGroovyClass(encounterModifierClassName));
            }
            return encounterModifiers.get(encounterModifierClassName);
        }
        return loadGroovyClass(encounterModifierClassName);
    }

    private EncounterModifier loadGroovyClass(String encounterModifierClassName) throws IOException, InstantiationException, IllegalAccessException {
        Class clazz = new GroovyClassLoader().parseClass(new File(getEncounterModifierClassPath(encounterModifierClassName)));
        return (EncounterModifier) clazz.newInstance();
    }

    private String getEncounterModifierClassPath(String encounterModifierClassName) {
        return OpenmrsUtil.getApplicationDataDirectory() + ENCOUNTER_MODIFIER_ALGORITHM_DIRECTORY + encounterModifierClassName ;
    }

    private boolean isGroovyCachingAllowed(){
        String globalProperty = Context.getAdministrationService().getGlobalProperty(ENCOUNTER_MODIFIER_GROOVY_ALLOW_CACHING);
        return Boolean.valueOf(globalProperty);
    }
}
