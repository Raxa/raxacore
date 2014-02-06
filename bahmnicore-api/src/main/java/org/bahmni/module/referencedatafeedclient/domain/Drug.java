package org.bahmni.module.referencedatafeedclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Drug {
    String id;
    String name;
    String genericName;
    DrugForm form;
    String strength;
    String strengthUnits;
    String route;
    Boolean isActive;
}
