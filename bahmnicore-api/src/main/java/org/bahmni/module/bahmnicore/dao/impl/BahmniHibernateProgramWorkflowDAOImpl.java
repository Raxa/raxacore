package org.bahmni.module.bahmnicore.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.dao.BahmniProgramWorkflowDAO;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.PatientProgramAttribute;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.HibernateProgramWorkflowDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BahmniHibernateProgramWorkflowDAOImpl extends HibernateProgramWorkflowDAO implements BahmniProgramWorkflowDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<ProgramAttributeType> getAllProgramAttributeTypes() {
        return sessionFactory.getCurrentSession().createCriteria(ProgramAttributeType.class).list();
    }

    @Override
    public ProgramAttributeType getProgramAttributeType(Integer id) {
        return (ProgramAttributeType) sessionFactory.getCurrentSession().get(ProgramAttributeType.class, id);
    }

    @Override
    public ProgramAttributeType getProgramAttributeTypeByUuid(String uuid) {
        return (ProgramAttributeType) sessionFactory.getCurrentSession().createCriteria(ProgramAttributeType.class).add(
                Restrictions.eq("uuid", uuid)).uniqueResult();
    }

    @Override
    public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType programAttributeType) {
        sessionFactory.getCurrentSession().saveOrUpdate(programAttributeType);
        return programAttributeType;
    }

    @Override
    public PatientProgramAttribute getPatientProgramAttributeByUuid(String uuid) {
        return (PatientProgramAttribute) sessionFactory.getCurrentSession().createCriteria(PatientProgramAttribute.class).add(Restrictions.eq("uuid", uuid)).uniqueResult();
    }

    @Override
    public void purgeProgramAttributeType(ProgramAttributeType type) {
        sessionFactory.getCurrentSession().delete(type);
    }

    @Override
    public PatientProgram getPatientProgramByUuid(String uuid) {
        return (BahmniPatientProgram) sessionFactory.getCurrentSession().createCriteria(BahmniPatientProgram.class).add(
                Restrictions.eq("uuid", uuid)).uniqueResult();
    }

    @Override
    public PatientProgram getPatientProgram(Integer patientProgramId) throws DAOException {
        return (BahmniPatientProgram) sessionFactory.getCurrentSession().get(BahmniPatientProgram.class, patientProgramId);
    }

    @Override
    public PatientProgram savePatientProgram(PatientProgram patientProgram) throws DAOException {
        CustomDatatypeUtil.saveAttributesIfNecessary((BahmniPatientProgram) patientProgram);
        return super.savePatientProgram(patientProgram);
    }

    @Override
    public List<BahmniPatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue) {
        Session session = sessionFactory.getCurrentSession();
        FlushMode flushMode = session.getFlushMode();
        session.setFlushMode(FlushMode.MANUAL);
        Query query;
        try {
            query = session.createQuery(
                    "SELECT bpp FROM BahmniPatientProgram bpp " +
                            "INNER JOIN bpp.attributes attr " +
                            "INNER JOIN attr.attributeType attr_type " +
                            "WHERE attr.valueReference = :attributeValue " +
                            "AND attr_type.name = :attributeName " +
                            "AND bpp.voided = 0")
                    .setParameter("attributeName", attributeName)
                    .setParameter("attributeValue", attributeValue);
            return query.list();
        } finally {
            session.setFlushMode(flushMode);
        }
    }

    public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
                                                   Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
            throws DAOException {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(BahmniPatientProgram.class);
        if (patient != null) {
            crit.add(Restrictions.eq("patient", patient));
        }
        if (program != null) {
            crit.add(Restrictions.eq("program", program));
        }
        if (minEnrollmentDate != null) {
            crit.add(Restrictions.ge("dateEnrolled", minEnrollmentDate));
        }
        if (maxEnrollmentDate != null) {
            crit.add(Restrictions.le("dateEnrolled", maxEnrollmentDate));
        }
        if (minCompletionDate != null) {
            crit.add(Restrictions.or(Restrictions.isNull("dateCompleted"), Restrictions.ge("dateCompleted",
                    minCompletionDate)));
        }
        if (maxCompletionDate != null) {
            crit.add(Restrictions.le("dateCompleted", maxCompletionDate));
        }
        if (!includeVoided) {
            crit.add(Restrictions.eq("voided", false));
        }
        return crit.list();
    }

    @Override
    public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patientIds, String attributeName) {
        Map<Object, Object> patientProgramAttributes = new HashMap<>();
        if (patientIds.isEmpty() || attributeName == null) {
            return patientProgramAttributes;
        }
        String commaSeperatedPatientIds = StringUtils.join(patientIds, ",");
        List list = sessionFactory.getCurrentSession().createSQLQuery(
                "SELECT p.patient_id as person_id, " +
                        " concat('{',group_concat(DISTINCT (coalesce(concat('\"',ppt.name,'\":\"', COALESCE (cn.name, ppa.value_reference),'\"'))) SEPARATOR ','),'}') AS patientProgramAttributeValue  " +
                        " from patient p " +
                        " join patient_program pp on p.patient_id = pp.patient_id and p.patient_id in (" + commaSeperatedPatientIds + ")" +
                        " join patient_program_attribute ppa on pp.patient_program_id = ppa.patient_program_id and ppa.voided=0" +
                        " join program_attribute_type ppt on ppa.attribute_type_id = ppt.program_attribute_type_id and ppt.name ='" + attributeName + "' "+
                        " LEFT OUTER JOIN concept_name cn on ppa.value_reference = cn.concept_id and cn.concept_name_type= 'FULLY_SPECIFIED' and cn.voided=0 and ppt.datatype like '%ConceptDataType%'" +
                        " group by p.patient_id")
                .addScalar("person_id", StandardBasicTypes.INTEGER)
                .addScalar("patientProgramAttributeValue", StandardBasicTypes.STRING)
                .list();

        for (Object o : list) {
            Object[] arr = (Object[]) o;
            patientProgramAttributes.put(arr[0], arr[1]);
        }

        return patientProgramAttributes;

    }

}
