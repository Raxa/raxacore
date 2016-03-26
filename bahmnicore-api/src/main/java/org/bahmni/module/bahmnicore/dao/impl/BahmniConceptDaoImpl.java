package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.BahmniConceptDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Repository
public class BahmniConceptDaoImpl implements BahmniConceptDao {

    private static final String WHITE_SPACE = "\\s+";
    private static final String WILD_CARD = "%";
    private static final String BASE_SEARCH_QUERY = "select answer " +
            "from ConceptAnswer as answer " +
            "join answer.answerConcept.names  as answerConceptNames " +
            "where answer.concept = :questionConcept " +
            " and answerConceptNames.voided = false ";
    @Autowired
    private SessionFactory sessionFactory;
    private String drugsWithConceptNamesForConceptSet = "concept_set csmembers " +
    "INNER JOIN concept c ON c.concept_id = csmembers.concept_id and csmembers.concept_set= (:conceptSetId) " +
    "RIGHT JOIN concept_name cn ON csmembers.concept_id = cn.concept_id and cn.voided = 0 " +
    "INNER JOIN drug d ON csmembers.concept_id = d.concept_id and d.retired = 0 ";

    @Override
    public Collection<Concept> searchByQuestion(Concept questionConcept, String searchQuery) {
        String[] queryArray = (searchQuery==null? "":searchQuery).split(WHITE_SPACE);
        StringBuffer queryStringBuffer = new StringBuffer(BASE_SEARCH_QUERY);
        appendSearchQueriesToBase(queryArray, queryStringBuffer);

        Query query = sessionFactory.getCurrentSession().createQuery(
                queryStringBuffer.toString());

        query.setEntity("questionConcept", questionConcept);
        for (int i = 0; i < queryArray.length; i++) {
            query.setString("query"+ i, searchBothSidesOf(queryArray[i]));
        }

        List<ConceptAnswer> answers = query.list();
        HashSet<Concept> resultConcepts = new HashSet<>();
        for (ConceptAnswer answer : answers) {
            resultConcepts.add(answer.getAnswerConcept());
        }
        return resultConcepts;
    }

    @Override
    public Concept getConceptByFullySpecifiedName(String fullySpecifiedConceptName) {
        List<Concept> concepts = sessionFactory.getCurrentSession()
                .createQuery("select concept " +
                        "from ConceptName as conceptName " +
                        "where conceptName.conceptNameType ='FULLY_SPECIFIED' " +
                        " and lower(conceptName.name)= lower(:fullySpecifiedName)")
                .setString("fullySpecifiedName", fullySpecifiedConceptName)
                .list();

        return concepts.size() > 0 ? concepts.get(0) : null;
    }

    @Override
    public Collection<Drug> getDrugByListOfConcepts(Collection<Concept> concepts) {
        return sessionFactory.getCurrentSession()
                .createQuery("select drug from  Drug as drug, ConceptSet as conceptSet " +
                        "where drug.concept in (:conceptIds) and conceptSet.concept = drug.concept order by conceptSet.sortWeight")
                .setParameterList("conceptIds", concepts)
                .list();
    }

    @Override
    public List searchDrugsByDrugName(Integer conceptSetId, String searchTerm) {
        List drugIds;
        if (null != searchTerm) {
            drugIds = sessionFactory.getCurrentSession()
                .createSQLQuery(getSqlForDrugsMatchingEitherConceptOrDrugName())
                .addScalar("drugId", StandardBasicTypes.INTEGER)
                .setParameter("conceptSetId", conceptSetId)
                .setString("searchPattern", searchBothSidesOf(searchTerm))
                .list();
        } else {
            drugIds = sessionFactory.getCurrentSession()
                .createSQLQuery(getSqlForAllDrugIds())
                .setParameter("conceptSetId", conceptSetId)
                .list();
        }
        return getDrugsByDrugIds(drugIds);
    }

    private String getSqlForDrugsMatchingEitherConceptOrDrugName() {
        return getDrugIdsFrom("(SELECT DISTINCT csmembers.sort_weight as sortWeight,d.drug_id as drugId "
            + "FROM " + drugsWithConceptNamesForConceptSet
            + "WHERE lower(cn.name) like (:searchPattern) or lower(d.name) LIKE (:searchPattern))");
    }

    private String getSqlForAllDrugIds() {
        return getDrugIdsFrom("SELECT DISTINCT csmembers.sort_weight as sortWeight,d.drug_id as drugId "
            + "FROM "
            + drugsWithConceptNamesForConceptSet);
    }

    private String getDrugIdsFrom(String drugs) {
        return "SELECT drugs.drugId as drugId FROM (" + drugs + ") as drugs ORDER BY drugs.sortWeight";
    }

    private List<Drug> getDrugsByDrugIds(List<Integer> drugsIdsInSortedOrder) {
        List<Drug> drugsInSortedOrder;
        drugsInSortedOrder = new ArrayList<>();
        for (Integer drugId : drugsIdsInSortedOrder) {
            drugsInSortedOrder.add(Context.getConceptService().getDrug(drugId));
        }
        return drugsInSortedOrder;
    }

    private void appendSearchQueriesToBase(String[] queryArray, StringBuffer queryStringBuffer) {
        for (int i = 0; i < queryArray.length; i++) {
            queryStringBuffer.append(" and lower(answerConceptNames.name) like :query" + i);
        }
    }

    private String searchBothSidesOf(String searchString) {
        return WILD_CARD + searchString.trim().toLowerCase() + WILD_CARD;
    }
}
