package org.raxa.module.raxacore.db.hibernate;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.db.DrugGroupDAO;

/**
 *
 * @author Yan
 */
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
	public List<DrugGroup> getDrugGroupList() throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		return crit.list();
	}
	
	@Override
	public DrugGroup saveDrugGroup(DrugGroup drugGroup) {
		sessionFactory.getCurrentSession().saveOrUpdate(drugGroup);
		return drugGroup;
	}
	
}
