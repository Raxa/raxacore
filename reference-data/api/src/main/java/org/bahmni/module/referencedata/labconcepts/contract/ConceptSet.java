package org.bahmni.module.referencedata.labconcepts.contract;

import java.util.ArrayList;
import java.util.List;

public class ConceptSet extends ConceptCommon {
    private List<String> children;

    public ConceptSet() {
    }

    public List<String> getChildren() {
        return children == null ? new ArrayList<String>() : children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }
}
