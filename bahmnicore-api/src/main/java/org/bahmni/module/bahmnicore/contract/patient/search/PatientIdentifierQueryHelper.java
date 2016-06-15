package org.bahmni.module.bahmnicore.contract.patient.search;

import org.apache.commons.lang.StringEscapeUtils;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class PatientIdentifierQueryHelper {

	private String identifier;
	private String identifierPrefix;

	public PatientIdentifierQueryHelper(String identifier, String identifierPrefix){
		this.identifier = identifier;
		this.identifierPrefix = identifierPrefix;
	}

	public String appendToWhereClause(String where){
		String identifierSearchCondition = getIdentifierSearchCondition(identifier, identifierPrefix);
		where = isEmpty(identifier) ? where : combine(where, "and", enclose(identifierSearchCondition));
		return where;
	}

	private String combine(String query, String operator, String condition) {
		return String.format("%s %s %s", query, operator, condition);
	}

	private String enclose(String value) {
		return String.format("(%s)", value);
	}


	private String getIdentifierSearchCondition(String identifier, String identifierPrefix) {
		return " identifier like  '" + identifierPrefix + "%" + StringEscapeUtils.escapeSql(identifier) + "%'";
	}

}
