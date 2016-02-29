package org.bahmni.module.bahmnicore.contract.patient.search;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientAttributeQueryHelper {
	private String customAttribute;
	private List<Integer> personAttributeTypeIds;

	public PatientAttributeQueryHelper(String customAttribute,List<Integer> personAttributeTypeIds) {
		this.customAttribute = customAttribute;
		this.personAttributeTypeIds = personAttributeTypeIds;
	}

	public String selectClause(String select){
			return select + ", group_concat(DISTINCT(coalesce(pattr_full_name.name, pattr_short_name.name, pattr.value)) SEPARATOR ',') as pattr_value, " +
					"concat('{',group_concat(DISTINCT (concat('\"',attrt.name,'\":\"',coalesce(pattr_full_name.name, pattr_short_name.name, pattr.value),'\"')) SEPARATOR ','),'}') AS customAttribute";
	}

	public String appendToJoinClause(String join){
		return join +" LEFT OUTER JOIN person_attribute_type attrt " +
					"on attrt.person_attribute_type_id in ("+ StringUtils.join(personAttributeTypeIds, ',')+") " +
				" LEFT OUTER JOIN person_attribute pattr on pattr.person_id = p.person_id and pattr.voided=0" +
					" and attrt.person_attribute_type_id = pattr.person_attribute_type_id" +
				"   LEFT OUTER JOIN concept_name as pattr_short_name  on pattr.value = CAST(pattr_short_name.concept_id AS CHAR) and pattr_short_name.concept_name_type = 'SHORT' " +
				"  LEFT OUTER JOIN concept_name as pattr_full_name  on pattr.value = CAST(pattr_full_name.concept_id AS CHAR) and pattr_full_name.concept_name_type = 'FULLY_SPECIFIED'";
	}

	public String appendToHavingClause(String having){
		if(StringUtils.isEmpty(customAttribute)){
			return having;
		}
		final String patientAttrHavingClause = " pattr_value like '%" + customAttribute + "%' ";
		return StringUtils.isEmpty(having)? combine(having, "having", patientAttrHavingClause): combine(having, "AND", patientAttrHavingClause);
	}

	public Map<String,Type> addScalarQueryResult(){
		Map<String,Type> scalarQueryResult = new HashMap<>();
		scalarQueryResult.put("customAttribute", StandardBasicTypes.STRING);
		return scalarQueryResult;
	}

	private String combine(String query, String operator, String condition) {
		return String.format("%s %s %s", query, operator, condition);
	}

	private String enclose(String value) {
		return String.format("(%s)", value);
	}

	public String appendToGroupByClause(String groupBy) {
		if(!StringUtils.isEmpty(groupBy)){
			groupBy = groupBy + ",";
		}

		return groupBy + "p.person_id, p.uuid , pi.identifier , pn.given_name , pn.middle_name , pn.family_name , p.gender , p.birthdate , p.death_date , p.date_created , v.uuid";
	}
}
