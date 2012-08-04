package org.raxa.module.raxacore.db.hibernate;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.db.DrugGroupDAO;

public class HibernateDrugGroupDAO implements DrugGroupDAO {
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public DrugGroup getDrugGroup(Integer id) {
		return (DrugGroup) sessionFactory.getCurrentSession().get(DrugGroup.class, id);
	}
	
	@Override
	public DrugGroup getDrugGroupByUuid(String uuid) {
		//		Criteria crit = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		//		crit.add(Restrictions.eq("uuid", uuid));
		//		return (DrugGroup) crit.uniqueResult();
		return (DrugGroup) sessionFactory.getCurrentSession().get(DrugGroup.class, uuid);
	}
	
	@Override
	public List<DrugGroup> getDrugGroupByName(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		criteria.add(Restrictions.like("name", name));
		criteria.add(Restrictions.like("retired", false));
		List<DrugGroup> drugGroups = new ArrayList<DrugGroup>();
		drugGroups.addAll(criteria.list());
		return drugGroups;
		
	}
	
	@Override
	public List<DrugGroup> getDrugGroupList() throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		return crit.list();
	}
	
	@Override
	public DrugGroup saveDrugGroup(DrugGroup drugGroup) {
		sessionFactory.getCurrentSession().saveOrUpdate(drugGroup);
		return drugGroup;
	}
	
	@Override
	public void deleteDrugGroup(DrugGroup drugGroup) throws DAOException {
		sessionFactory.getCurrentSession().delete(drugGroup);
	}
	
	@Override
	public DrugGroup updateDrugGroup(DrugGroup drugGroup) throws DAOException {
		sessionFactory.getCurrentSession().update(drugGroup);
		return drugGroup;
	}
}
