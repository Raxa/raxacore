package org.bahmni.module.bahmnicore;

import org.bahmni.module.bahmnicore.datamigration.ExecutionMode;

public interface BahmniCoreApiProperties {
    public String getImageDirectory();
    public ExecutionMode getExecutionMode();
}
