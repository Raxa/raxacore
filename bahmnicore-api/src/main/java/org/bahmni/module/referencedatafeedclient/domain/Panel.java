package org.bahmni.module.referencedatafeedclient.domain;

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
    private static final String PANEL_SUFFIX = " (Panel)";
    String id;
    String name;
    String description;
    String shortName;
    Boolean isActive = true;
    Sample sample;
    Set<Test> tests = new HashSet<>();
    double sortOrder;

    public void suffixPanelToName() {
        name = name + PANEL_SUFFIX;
    }
}
