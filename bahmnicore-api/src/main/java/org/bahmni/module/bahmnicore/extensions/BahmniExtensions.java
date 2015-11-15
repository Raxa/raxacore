package org.bahmni.module.bahmnicore.extensions;

import groovy.lang.GroovyClassLoader;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.dao.impl.ApplicationDataDirectory;
import org.bahmni.module.bahmnicore.dao.impl.ApplicationDataDirectoryImpl;
import org.bahmni.module.bahmnicore.encounterModifier.EncounterModifier;
import org.openmrs.module.bahmniemrapi.drugogram.contract.BaseTreatmentRegimenExtension;
import org.openmrs.module.bahmniemrapi.drugogram.contract.TreatmentRegimenExtension;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class BahmniExtensions {

	private static final Logger log = Logger.getLogger(BahmniExtensions.class);

	private GroovyClassLoader groovyClassLoader;

	private ApplicationDataDirectory applicationDataDirectory;

	public BahmniExtensions() {
		groovyClassLoader = new GroovyClassLoader();
		applicationDataDirectory = new ApplicationDataDirectoryImpl();
	}

	public TreatmentRegimenExtension getTreatmentRegimenExtension() {
		File treatmentRegimenExtensionGroovyPath = applicationDataDirectory
				.getFileFromConfig("openmrs"+ File.separator +"treatmentRegimenExtension" + File.separator + "TreatmentRegimenExtension.groovy");
		if (!treatmentRegimenExtensionGroovyPath.exists()) {
			return new BaseTreatmentRegimenExtension();
		}

		try {
			Class clazz = groovyClassLoader.parseClass(treatmentRegimenExtensionGroovyPath);
			return (TreatmentRegimenExtension) clazz.newInstance();
		}
		catch (IOException e) {
			log.error("Problem with the groovy class " + treatmentRegimenExtensionGroovyPath, e);
		}
		catch (InstantiationException e) {
			log.error("The groovy class " + treatmentRegimenExtensionGroovyPath + " cannot be instantiated", e);
		}
		catch (IllegalAccessException e) {
			log.error("Problem with the groovy class " + treatmentRegimenExtensionGroovyPath, e);
		}

		return new BaseTreatmentRegimenExtension();
	}

}
