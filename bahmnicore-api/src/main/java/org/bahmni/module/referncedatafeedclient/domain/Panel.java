package org.bahmni.module.referncedatafeedclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Panel {
    String id;
    String name;
    String description;
    String shortName;
    boolean active = true;
    Sample sample;
    Set<Test> tests = new HashSet<>();
}
