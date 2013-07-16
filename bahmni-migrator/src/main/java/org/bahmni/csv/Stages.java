package org.bahmni.csv;

import java.util.ArrayList;
import java.util.List;

class Stages<T extends CSVEntity>  {
    private List<Stage<T>> stages = new ArrayList<>();

    private int index = 0;

    public void addStage(Stage<T> aStage) {
        stages.add(aStage);
    }

    public boolean hasMoreStages() {
        return index < stages.size();
    }

    public Stage<T> nextStage() {
        Stage<T> aStage = stages.get(index);
        index++;
        return aStage;
    }
}
