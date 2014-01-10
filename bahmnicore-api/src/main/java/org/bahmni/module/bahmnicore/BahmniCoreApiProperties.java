package org.bahmni.module.bahmnicore;

import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;

public interface BahmniCoreApiProperties {
    String getImageDirectory();
    ExecutionMode getExecutionMode();
    String getPatientImagesUrl();
    String getDocumentBaseDirectory();
}
