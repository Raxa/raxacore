package org.bahmni.module.bahmnicore.dao.impl;

import org.bahmni.module.bahmnicore.dao.PersonAttributeTypeDao;
import org.bahmni.module.bahmnicore.model.BahmniPersonAttributeType;
import org.bahmni.module.bahmnicore.model.ResultList;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonAttributeTypeDaoImpl implements PersonAttributeTypeDao {
	
	@Autowired
	private SessionFactory sessionFactory;

    @Override
    public List<BahmniPersonAttributeType> getAll() {
        SQLQuery sqlQuery = sessionFactory
                .getCurrentSession()
                .createSQLQuery(
                        "Select distinct name from person_attribute_type"
                             ).setResultSetMapping(BahmniPersonAttributeType.class.toString());
        ResultList resultList = new ResultList(sqlQuery.list());
        return new ArrayList<BahmniPersonAttributeType>();
    }

}
