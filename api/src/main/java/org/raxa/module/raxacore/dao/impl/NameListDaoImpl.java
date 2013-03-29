package org.raxa.module.raxacore.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.PersonName;
import org.raxa.module.raxacore.dao.NameListDao;
import org.raxa.module.raxacore.model.NameList;

public class NameListDaoImpl implements NameListDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public NameList getLastNames(String query) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonName.class);
		criteria.add(Restrictions.ilike("familyName", query + "%"));
		criteria.setProjection(Projections.distinct(Projections.property("familyName")));
		return new NameList(criteria.list());
	}
}
