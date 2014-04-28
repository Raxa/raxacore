package org.bahmni.module.referencedatafeedclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestUnitOfMeasure {
    String id;
    String name;
    Boolean isActive;

    public TestUnitOfMeasure(String id) {
        this(id, null, true);
    }
}
