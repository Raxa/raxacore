package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.dao.BahmniPatientDao;
import org.bahmni.module.bahmnicore.model.NameSearchParameter;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Repository
public class BahmniPatientDaoImpl implements BahmniPatientDao {
    public static final String PATIENT_IDENTIFIER_PARAM = "patientIdentifier";
    public static final String LIMIT_PARAM = "limit";
    public static final String OFFSET_PARAM = "offset";
    public static final String VILLAGE_PARAM = "village";

    public static final String FIND = "select p.uuid as uuid, pi.identifier as identifier, pn.given_name as givenName, pn.middle_name as middleName, pn.family_name as familyName, p.gender as gender, p.birthdate as birthDate," +
            " p.death_date as deathDate, pa.city_village as cityVillage, p.date_created as dateCreated, v.uuid as activeVisitUuid " +
            " from patient pat inner join person p on pat.patient_id=p.person_id " +
            " left join person_name pn on pn.person_id = p.person_id" +
            " left join person_address pa on p.person_id=pa.person_id and pa.voided = 'false'" +
            " inner join patient_identifier pi on pi.patient_id = p.person_id " +
            " left outer join visit v on v.patient_id = pat.patient_id and v.date_stopped is null " +
            " where p.voided = 'false' and pn.voided = 'false' and pn.preferred=true";

    public static final String BY_ID = "pi.identifier like :" + PATIENT_IDENTIFIER_PARAM;
    public static final String BY_NAME_PARTS = "concat(coalesce(given_name, ''), coalesce(middle_name, ''), coalesce(family_name, '')) like";
    public static final String BY_VILLAGE = "pa.city_village like :" + VILLAGE_PARAM;
    public static final String ORDER_BY = "order by p.date_created desc LIMIT :" + LIMIT_PARAM + " OFFSET :" + OFFSET_PARAM;

    private SessionFactory sessionFactory;

    @Autowired
    public BahmniPatientDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<PatientResponse> getPatients(String identifier, String name, String village, Integer length, Integer offset) {
        Session currentSession = sessionFactory.getCurrentSession();

        NameSearchParameter nameSearchParameter = NameSearchParameter.create(name);
        String nameSearchCondition = getNameSearchCondition(nameSearchParameter);
        String query = FIND;
        query = isEmpty(identifier) ? query : combine(query, "and", enclose(BY_ID));
        query = isEmpty(nameSearchCondition) ? query : combine(query, "and", enclose(nameSearchCondition));
        query = isEmpty(village) ? query : combine(query, "and", enclose(BY_VILLAGE));
        query += ORDER_BY;

        Query sqlQuery = currentSession
                .createSQLQuery(query)
                .addScalar("uuid", StandardBasicTypes.STRING)
                .addScalar("identifier", StandardBasicTypes.STRING)
                .addScalar("givenName", StandardBasicTypes.STRING)
                .addScalar("middleName", StandardBasicTypes.STRING)
                .addScalar("familyName", StandardBasicTypes.STRING)
                .addScalar("gender", StandardBasicTypes.STRING)
                .addScalar("birthDate", StandardBasicTypes.DATE)
                .addScalar("deathDate", StandardBasicTypes.DATE)
                .addScalar("cityVillage", StandardBasicTypes.STRING)
                .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
                .addScalar("activeVisitUuid", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(PatientResponse.class));

        if (isNotEmpty(identifier))
            sqlQuery.setParameter(PATIENT_IDENTIFIER_PARAM, "%" + identifier + "%");
        if (isNotEmpty(village))
            sqlQuery.setParameter(VILLAGE_PARAM, village + "%");
        sqlQuery.setParameter(LIMIT_PARAM, length);
        sqlQuery.setParameter(OFFSET_PARAM, offset);

        return sqlQuery.list();
    }

    @Override
    public Patient getPatient(String identifier) {
        Session currentSession = sessionFactory.getCurrentSession();
        List<PatientIdentifier> ident = currentSession.createQuery("from PatientIdentifier where identifier = :ident").setString("ident", identifier).list();
        if (!ident.isEmpty()) {
            return ident.get(0).getPatient();
        }
        return null;
    }

    private String getNameSearchCondition(NameSearchParameter nameSearchParameter) {
        if (nameSearchParameter.isEmpty())
            return "";
        else {
            String query_by_name_parts = "";
            for (String part : nameSearchParameter.getNameParts()) {
                if (!query_by_name_parts.equals("")) {
                    query_by_name_parts +=" and " + BY_NAME_PARTS + " '" + part + "'";
                } else {
                    query_by_name_parts += BY_NAME_PARTS + " '" + part + "'";
                }
            }
            return query_by_name_parts;
        }
    }

    private static String combine(String query, String operator, String condition) {
        return String.format("%s %s %s", query, operator, condition);
    }

    private static String enclose(String value) {
        return String.format("(%s)", value);
    }
}
