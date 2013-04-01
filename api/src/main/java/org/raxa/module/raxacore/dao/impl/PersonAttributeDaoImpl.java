package org.raxa.module.raxacore.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.raxa.module.raxacore.dao.PersonAttributeDoa;
import org.raxa.module.raxacore.model.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PersonAttributeDaoImpl implements PersonAttributeDoa {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public ResultList getUnique(String personAttribute, String query) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
		    "Select value from person_attribute, person_attribute_type "
		            + "where person_attribute.person_attribute_type_id = person_attribute_type.person_attribute_type_id "
		            + "and person_attribute_type.name = :name and person_attribute.value like :value");
		sqlQuery.setParameter("name", personAttribute);
		sqlQuery.setParameter("value", query + "%");
		return new ResultList(sqlQuery.list());
	}
}
