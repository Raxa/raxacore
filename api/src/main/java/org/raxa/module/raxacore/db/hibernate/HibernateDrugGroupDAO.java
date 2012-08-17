package org.raxa.module.raxacore.db.hibernate;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.EncounterType;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.db.DrugGroupDAO;

public class HibernateDrugGroupDAO implements DrugGroupDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see
	 * org.raxa.module.db.DrugGroupDAO#saveDrugGroup(org.raxa.module.raxacore.DrugGroup)
	 */
	@Override
	public DrugGroup saveDrugGroup(DrugGroup drugGroup) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(drugGroup);
		return drugGroup;
	}
	
	/**
	 * @see
	 * org.raxa.module.db.DrugGroupDAO#deleteDrugGroup(org.raxa.module.raxacore.DrugGroup)
	 */
	@Override
	public void deleteDrugGroup(DrugGroup drugGroup) throws DAOException {
		sessionFactory.getCurrentSession().delete(drugGroup);
	}
	
	/**
	 * @see org.raxa.module.db.DrugGroupDAO#getDrugGroup(Integer)
	 */
	@Override
	public DrugGroup getDrugGroup(Integer drugGroupId) throws DAOException {
		return (DrugGroup) sessionFactory.getCurrentSession().get(DrugGroup.class, drugGroupId);
	}
	
	/**
	 * @see org.raxa.module.db.DrugGroupDAO#getDrugGroupByUuid(String)
	 */
	@Override
	public DrugGroup getDrugGroupByUuid(String uuid) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (DrugGroup) criteria.uniqueResult();
	}
	
	/**
	 * @see org.raxa.module.db.DrugGroupDAO#getDrugGroupByName(String)
	 */
	@Override
	public List<DrugGroup> getDrugGroupByName(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		criteria.add(Restrictions.like("name", name));
		criteria.add(Restrictions.like("retired", false));
		List<DrugGroup> patients = new ArrayList<DrugGroup>();
		patients.addAll(criteria.list());
		return patients;
		
	}
	
	/**
	 * @see org.raxa.module.db.DrugGroupDAO#getAllDrugGroup()
	 */
	@Override
	public List<DrugGroup> getAllDrugGroup(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugGroup.class);
		if (includeRetired == false) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	/**
	 * @see org.raxa.module.db.DrugGroupDAO#updateDrugGroup(Integer)
	 */
	@Override
	public DrugGroup updateDrugGroup(DrugGroup drugGroup) throws DAOException {
		sessionFactory.getCurrentSession().update(drugGroup);
		return drugGroup;
	}
}
