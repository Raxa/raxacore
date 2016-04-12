package org.bahmni.module.bahmnicore.contract.patient.search;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.customdatatype.datatype.CodedConceptDatatype;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PatientSearchBuilder {
	public static final String SELECT_STATEMENT = "select " +
			"p.uuid as uuid, " +
			"p.person_id as personId, " +
			"pi.identifier as identifier, " +
			"pn.given_name as givenName, " +
			"pn.middle_name as middleName, " +
			"pn.family_name as familyName, " +
			"p.gender as gender, " +
			"p.birthdate as birthDate, " +
			"p.death_date as deathDate, " +
			"p.date_created as dateCreated, " +
			"v.uuid as activeVisitUuid, " +
			"(CASE va.value_reference WHEN 'Admitted' THEN TRUE ELSE FALSE END) as hasBeenAdmitted ";
	public static final String WHERE_CLAUSE = " where p.voided = 'false' and pn.voided = 'false' and pn.preferred=true ";
	public static final String FROM_TABLE = " from patient pat " ;
	public static final String JOIN_CLAUSE =  " inner join person p on pat.patient_id=p.person_id " +
			" left join person_name pn on pn.person_id = p.person_id" +
			" left join person_address pa on p.person_id=pa.person_id and pa.voided = 'false'" +
			" inner join patient_identifier pi on pi.patient_id = p.person_id " +
			" left outer join visit v on v.patient_id = pat.patient_id and v.date_stopped is null " +
			" left outer join visit_attribute va on va.visit_id = v.visit_id " +
			"   and va.attribute_type_id = (select visit_attribute_type_id from visit_attribute_type where name='Admission Status') " +
			"   and va.voided = 0";
	private static final String GROUP_BY_KEYWORD = " group by ";
	public static final String ORDER_BY = " order by p.date_created desc LIMIT :limit OFFSET :offset";
	private static final String LIMIT_PARAM = "limit";
	private static final String OFFSET_PARAM = "offset";


	private String select;
	private String where;
	private String from;
	private String join;
	private String groupBy;
	private String orderBy;
	private SessionFactory sessionFactory;
	private Map<String,Type> types;
	private String having;


	public PatientSearchBuilder(SessionFactory sessionFactory){
		select = SELECT_STATEMENT;
		where = WHERE_CLAUSE;
		from  = FROM_TABLE;
		join = JOIN_CLAUSE;
		orderBy = ORDER_BY;
		groupBy = "";
		having = "";
		this.sessionFactory = sessionFactory;
		types = new HashMap<>();

	}

	public PatientSearchBuilder withPatientName(String name){
		PatientNameQueryHelper patientNameQueryHelper = new PatientNameQueryHelper(name);
		where = patientNameQueryHelper.appendToWhereClause(where);
		return this;
	}

	public PatientSearchBuilder withPatientAddress(String addressFieldName,String addressFieldValue){
		PatientAddressFieldQueryHelper patientAddressQueryHelper = new PatientAddressFieldQueryHelper(addressFieldName,addressFieldValue);
		where = patientAddressQueryHelper.appendToWhereClause(where);
		select = patientAddressQueryHelper.selectClause(select);
		groupBy = patientAddressQueryHelper.appendToGroupByClause(groupBy);
		types.putAll(patientAddressQueryHelper.addScalarQueryResult());
		return this;
	}

	public PatientSearchBuilder withPatientIdentifier(String identifier,String identifierPrefix){
		PatientIdentifierQueryHelper patientIdentifierQueryHelper = new PatientIdentifierQueryHelper(identifier,identifierPrefix);
		where = patientIdentifierQueryHelper.appendToWhereClause(where);
		return this;
	}

	public PatientSearchBuilder withPatientAttributes(String customAttribute, List<Integer> personAttributeIds){
		if(personAttributeIds.size() == 0){
			return this;
		}

		PatientAttributeQueryHelper patientAttributeQueryHelper = new PatientAttributeQueryHelper(customAttribute,personAttributeIds);
		select = patientAttributeQueryHelper.selectClause(select);
		join = patientAttributeQueryHelper.appendToJoinClause(join);
		groupBy = patientAttributeQueryHelper.appendToGroupByClause(groupBy);
		having = patientAttributeQueryHelper.appendToHavingClause(having);
		types.putAll(patientAttributeQueryHelper.addScalarQueryResult());
		return this;
	}

	public PatientSearchBuilder withProgramAttributes(String programAttribute, ProgramAttributeType programAttributeType){
		if(programAttributeType == null){
			return this;
		}

		Integer programAttributeTypeId = programAttributeType.getProgramAttributeTypeId();
		boolean isAttributeValueCodedConcept = programAttributeType.getDatatypeClassname().equals(CodedConceptDatatype.class.getCanonicalName());


		PatientProgramAttributeQueryHelper programAttributeQueryHelper;
		if (isAttributeValueCodedConcept) {
			programAttributeQueryHelper = new ProgramAttributeCodedValueQueryHelper(programAttribute,
					programAttributeTypeId);
		} else {
			programAttributeQueryHelper = new PatientProgramAttributeQueryHelper(programAttribute, programAttributeTypeId);
		}

		select = programAttributeQueryHelper.selectClause(select);
		join = programAttributeQueryHelper.appendToJoinClause(join);
		groupBy = programAttributeQueryHelper.appendToGroupByClause(groupBy);
		where = programAttributeQueryHelper.appendToWhereClause(where);
		types.putAll(programAttributeQueryHelper.addScalarQueryResult());
		return this;
	}

	public SQLQuery buildSqlQuery(Integer limit, Integer offset){
		String query = select + from + join + where + GROUP_BY_KEYWORD + groupBy  + having + orderBy;

		SQLQuery sqlQuery = sessionFactory.getCurrentSession()
				.createSQLQuery(query)
				.addScalar("uuid", StandardBasicTypes.STRING)
				.addScalar("identifier", StandardBasicTypes.STRING)
				.addScalar("givenName", StandardBasicTypes.STRING)
				.addScalar("personId", StandardBasicTypes.INTEGER)
				.addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("familyName", StandardBasicTypes.STRING)
				.addScalar("gender", StandardBasicTypes.STRING)
				.addScalar("birthDate", StandardBasicTypes.DATE)
				.addScalar("deathDate", StandardBasicTypes.DATE)
				.addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("activeVisitUuid", StandardBasicTypes.STRING)
				.addScalar("hasBeenAdmitted", StandardBasicTypes.BOOLEAN);

		Iterator<Map.Entry<String,Type>> iterator = types.entrySet().iterator();

		while(iterator.hasNext()){
			Map.Entry<String,Type> entry = iterator.next();
			sqlQuery.addScalar(entry.getKey(),entry.getValue());
		}

		sqlQuery.setParameter(LIMIT_PARAM, limit);
		sqlQuery.setParameter(OFFSET_PARAM, offset);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(PatientResponse.class));
		return sqlQuery;
	}

}
