package org.bahmni.module.referencedata.labconcepts.contract;

import org.openmrs.ConceptAnswer;

import java.util.ArrayList;
import java.util.Collection;

public class LabTest extends Resource {
    private String description;
    private String resultType;
    private String testUnitOfMeasure;
    private Double sortOrder;
    public static final String LAB_TEST_CONCEPT_CLASS = "LabTest";
    private Collection<CodedTestAnswer> codedTestAnswer;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getTestUnitOfMeasure() {
        return testUnitOfMeasure;
    }

    public void setTestUnitOfMeasure(String testUnitOfMeasure) {
        this.testUnitOfMeasure = testUnitOfMeasure;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Double getSortOrder() {
        return sortOrder;
    }

    public void setCodedTestAnswer(Collection<ConceptAnswer> conceptAnswers) {
        codedTestAnswer = new ArrayList<>();
        for (ConceptAnswer conceptAnswer : conceptAnswers) {
            CodedTestAnswer ans = new CodedTestAnswer();
            ans.setName(conceptAnswer.getAnswerConcept().getName().getName());
            ans.setUuid(conceptAnswer.getAnswerConcept().getUuid());
            codedTestAnswer.add(ans);
        }
    }

    public Collection<CodedTestAnswer> getCodedTestAnswer() {
        return codedTestAnswer;
    }
}
