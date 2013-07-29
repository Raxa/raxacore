package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.dao.BahmniPatientDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Repository
public class BahmniPatientDaoImpl implements BahmniPatientDao {

    public static final String FIND = "select p.uuid as uuid, pi.identifier as identifier, pn.given_name as givenName, pn.family_name as familyName, p.gender as gender, p.birthdate as birthDate," +
            " p.death_date as deathDate, pa.city_village as cityVillage, p.date_created as dateCreated" +
            " from patient pat inner join person p on pat.patient_id=p.person_id " +
            " left join person_name pn on pn.person_id = p.person_id" +
            " left join person_address pa on p.person_id=pa.person_id " +
            " inner join patient_identifier pi on pi.patient_id = p.person_id " +
            " where p.voided = 'false' and pn.preferred='true'";

    public static final String BY_ID = " and ( pi.identifier = :" + BahmniPatientDaoImpl.PATIENT_IDENTIFIER_PARAM + " )";
    public static final String BY_NAME = " and ( pn.given_name like :" + BahmniPatientDaoImpl.NAME_PARAM + " or pn.family_name like :" + BahmniPatientDaoImpl.NAME_PARAM + " )";
    public static final String BY_VILLAGE = " and ( pa.city_village like :" + BahmniPatientDaoImpl.VILLAGE_PARAM + " )";
    public static final String ORDER_BY = " order by p.date_created desc LIMIT 50";

    public static final String PATIENT_IDENTIFIER_PARAM = "patientIdentifier";
    public static final String NAME_PARAM = "name";
    public static final String VILLAGE_PARAM = "village";

    private SessionFactory sessionFactory;

    @Autowired
    public BahmniPatientDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<PatientResponse> getPatients(String identifier, String name, String village) {
        Session currentSession = sessionFactory.getCurrentSession();

        String query = FIND;
        query += isEmpty(identifier) ? "" : BY_ID;
        query += isEmpty(name) ? "" : BY_NAME;
        query += isEmpty(village) ? "" : BY_VILLAGE;
        query += ORDER_BY;

        Query sqlQuery = currentSession
                .createSQLQuery(query)
                .addScalar("uuid", StandardBasicTypes.STRING)
                .addScalar("identifier", StandardBasicTypes.STRING)
                .addScalar("givenName", StandardBasicTypes.STRING)
                .addScalar("familyName", StandardBasicTypes.STRING)
                .addScalar("gender", StandardBasicTypes.STRING)
                .addScalar("birthDate", StandardBasicTypes.DATE)
                .addScalar("deathDate", StandardBasicTypes.DATE)
                .addScalar("cityVillage", StandardBasicTypes.STRING)
                .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
                .setResultTransformer(Transformers.aliasToBean(PatientResponse.class));

        if (isNotEmpty(identifier))
            sqlQuery.setParameter(PATIENT_IDENTIFIER_PARAM, identifier);
        if (isNotEmpty(name))
            sqlQuery.setParameter(NAME_PARAM, name + "%");
        if (isNotEmpty(village))
            sqlQuery.setParameter(VILLAGE_PARAM, village + "%");

        return sqlQuery.list();
    }

}
