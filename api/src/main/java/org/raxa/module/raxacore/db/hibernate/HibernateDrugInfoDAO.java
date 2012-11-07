package org.raxa.module.raxacore.db.hibernate;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.db.DrugInfoDAO;

/**
 * Accesses raxacore_drug_info from DrugInfo
 */
public class HibernateDrugInfoDAO implements DrugInfoDAO {
	
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
	 * @see org.raxa.module.db.DrugInfoDAO#saveDrugInfo(org.raxa.module.raxacore.DrugInfo)
	 */
	@Override
	public DrugInfo saveDrugInfo(DrugInfo drugInfo) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(drugInfo);
		return drugInfo;
	}
	
	/**
	 * @see org.raxa.module.db.DrugInfoDAO#deleteDrugInfo(org.raxa.module.raxacore.DrugInfo)
	 */
	@Override
	public void deleteDrugInfo(DrugInfo drugInfo) throws DAOException {
		sessionFactory.getCurrentSession().delete(drugInfo);
	}
	
	/**
	 * @see org.raxa.module.db.DrugInfoDAO#getDrugInfo(Integer)
	 */
	@Override
	public DrugInfo getDrugInfo(Integer drugInfoId) throws DAOException {
		return (DrugInfo) sessionFactory.getCurrentSession().get(DrugInfo.class, drugInfoId);
	}
	
	/**
	 * @see org.raxa.module.db.DrugInfoDAO#getDrugInfoByUuid(String)
	 */
	@Override
	public DrugInfo getDrugInfoByUuid(String uuid) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugInfo.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (DrugInfo) criteria.uniqueResult();
	}
	
	/**
	 * @see org.raxa.module.db.DrugInfoDAO#getDrugInfoByName(String)
	 */
	@Override
	public List<DrugInfo> getDrugInfoByName(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugInfo.class);
		criteria.add(Restrictions.like("name", name));
		criteria.add(Restrictions.like("retired", false));
		List<DrugInfo> patients = new ArrayList<DrugInfo>();
		patients.addAll(criteria.list());
		return patients;
		
	}
	
	@Override
	public DrugInfo getDrugInfoByDrug(Integer id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugInfo.class);
		criteria.add(Restrictions.eq("drugId", id));
		return (DrugInfo) criteria.uniqueResult();
	}
	
	/**
	 * @see org.raxa.module.db.DrugInfoDAO#getAllDrugInfo()
	 */
	@Override
	public List<DrugInfo> getAllDrugInfo(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DrugInfo.class);
		if (includeRetired == false) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	/**
	 * @see org.raxa.module.db.DrugInfoDAO#updateDrugInfo(Integer)
	 */
	@Override
	public DrugInfo updateDrugInfo(DrugInfo drugInfo) throws DAOException {
		sessionFactory.getCurrentSession().update(drugInfo);
		return drugInfo;
	}
}
