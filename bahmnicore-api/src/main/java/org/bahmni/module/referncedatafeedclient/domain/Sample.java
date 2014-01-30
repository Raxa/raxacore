package org.bahmni.module.referncedatafeedclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample {
    String id;
    String name;
    String shortName;

    public Sample(String id) {
        this(id, null, null);
    }
}
