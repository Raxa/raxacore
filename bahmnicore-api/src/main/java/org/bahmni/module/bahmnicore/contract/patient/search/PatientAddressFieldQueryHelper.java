package org.bahmni.module.bahmnicore.contract.patient.search;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PatientAddressFieldQueryHelper {
	private String addressFieldValue;
	private String addressFieldName;

	public PatientAddressFieldQueryHelper(String addressFieldName,String addressFieldValue){
		this.addressFieldName = addressFieldName;
		this.addressFieldValue = addressFieldValue;
	}

	public String selectClause(String select){
		return select + ",pa."+addressFieldName+" as addressFieldValue";
	}

	public String appendToWhereClause(String where){
		if (isEmpty(addressFieldValue)) {
			return where;
		}
		return combine(where, "and", enclose(" " +addressFieldName+ " like '%" + addressFieldValue + "%'"));

	}

	private String combine(String query, String operator, String condition) {
		return String.format("%s %s %s", query, operator, condition);
	}

	private String enclose(String value) {
		return String.format("(%s)", value);
	}

	public Map<String,Type> addScalarQueryResult(){
		Map<String,Type> scalarQueryResult = new HashMap<>();
		scalarQueryResult.put("addressFieldValue", StandardBasicTypes.STRING);
		return scalarQueryResult;
	}

	public String appendToGroupByClause(String groupBy) {
		if(!isEmpty(groupBy)){
			groupBy = groupBy + ",";
		}
		groupBy = groupBy + addressFieldName + ",p.person_id, p.uuid , pi.identifier , pn.given_name , pn.middle_name , pn.family_name , p.gender , p.birthdate , p.death_date , p.date_created , v.uuid";
		return groupBy;
	}
}
