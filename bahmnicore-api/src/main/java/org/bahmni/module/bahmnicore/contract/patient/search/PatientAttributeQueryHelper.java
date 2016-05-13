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
			return select + ", " +
					"concat('{',group_concat(DISTINCT (coalesce(concat('\"',attrt.name,'\":\"', pattrln.value,'\"'))) SEPARATOR ','),'}') AS customAttribute";
	}

	public String appendToJoinClause(String join){
		return join + " LEFT OUTER JOIN person_attribute pattrln on pattrln.person_id = p.person_id " +
				" LEFT OUTER JOIN person_attribute_type attrt on attrt.person_attribute_type_id = pattrln.person_attribute_type_id and attrt.person_attribute_type_id in ("+ StringUtils.join(personAttributeTypeIds, ',')+") ";
	}

	public String appendToWhereClause(String where){
		if(StringUtils.isEmpty(customAttribute)){
			return where;
		}
		return combine(where, "and", enclose(" pattrln.value like "+ "'%" + customAttribute + "%'"));
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
