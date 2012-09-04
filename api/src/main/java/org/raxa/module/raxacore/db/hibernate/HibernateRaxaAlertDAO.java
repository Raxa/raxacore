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
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.RaxaAlert;
import org.raxa.module.raxacore.db.RaxaAlertDAO;

/**
 * Accesses raxacore_raxaalert from RaxaAlert
 */
public class HibernateRaxaAlertDAO implements RaxaAlertDAO {
	
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
	 * @see org.raxa.module.db.RaxaAlertDAO#saveRaxaAlert(org.raxa.module.raxacore.RaxaAlert)
	 */
	@Override
	public RaxaAlert saveRaxaAlert(RaxaAlert raxaAlert) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(raxaAlert);
		return raxaAlert;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#deleteRaxaAlert(org.raxa.module.raxacore.RaxaAlert)
	 */
	@Override
	public void deleteRaxaAlert(RaxaAlert raxaAlert) throws DAOException {
		sessionFactory.getCurrentSession().delete(raxaAlert);
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlert(Integer)
	 */
	@Override
	public RaxaAlert getRaxaAlert(Integer raxaAlertId) throws DAOException {
		return (RaxaAlert) sessionFactory.getCurrentSession().get(RaxaAlert.class, raxaAlertId);
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlertByUuid(String)
	 */
	@Override
	public RaxaAlert getRaxaAlertByUuid(String uuid) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (RaxaAlert) criteria.uniqueResult();
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlertByName(String)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByName(String name, boolean includeSeen) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.eq("name", name));
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		List<RaxaAlert> alerts = new ArrayList<RaxaAlert>();
		alerts.addAll(criteria.list());
		return alerts;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlertByAlertType(String)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByAlertType(String alertType, boolean includeSeen) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.like("alertType", alertType));
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		List<RaxaAlert> alerts = new ArrayList<RaxaAlert>();
		alerts.addAll(criteria.list());
		return alerts;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlertByPatientId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByPatientId(Integer patientId, boolean includeSeen) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.eq("patientId", patientId));
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		List<RaxaAlert> alerts = new ArrayList<RaxaAlert>();
		alerts.addAll(criteria.list());
		return alerts;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlertByProviderSentId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderSentId(Integer providerSentId, boolean includeSeen) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.eq("providerSentId", providerSentId));
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		List<RaxaAlert> alerts = new ArrayList<RaxaAlert>();
		alerts.addAll(criteria.list());
		return alerts;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getRaxaAlertByProviderRecipientId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderRecipientId(Integer providerRecipientId, boolean includeSeen)
	        throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.eq("providerRecipientId", providerRecipientId));
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		List<RaxaAlert> alerts = new ArrayList<RaxaAlert>();
		alerts.addAll(criteria.list());
		return alerts;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#getAllRaxaAlert()
	 */
	@Override
	public List<RaxaAlert> getAllRaxaAlerts(boolean includeSeen) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		return criteria.list();
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#updateRaxaAlert(Integer)
	 */
	@Override
	public RaxaAlert updateRaxaAlert(RaxaAlert raxaAlert) throws DAOException {
		sessionFactory.getCurrentSession().update(raxaAlert);
		return raxaAlert;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#markRaxaAlertAsSeen(boolean)
	 */
	@Override
	public RaxaAlert markRaxaAlertAsSeen(RaxaAlert raxaAlert) {
		raxaAlert.setSeen(true);
		return raxaAlert;
	}
	
	/**
	 * @see org.raxa.module.db.RaxaAlertDAO#voidRaxaAlert(RaxaAlert)
	 */
	@Override
	public RaxaAlert voidRaxaAlert(RaxaAlert raxaAlert, String reason) {
		if (reason == null) {
			throw new IllegalArgumentException("The argument 'reason' is required and so cannot be null");
		}
		
		raxaAlert.setVoided(true);
		raxaAlert.setVoidedBy(Context.getAuthenticatedUser());
		raxaAlert.setDateVoided(new Date());
		raxaAlert.setVoidReason(reason);
		saveRaxaAlert(raxaAlert);
		return raxaAlert;
	}
	
	@Override
	public List<RaxaAlert> getRaxaAlertByToLocationId(Integer id, boolean includeSeen) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RaxaAlert.class);
		criteria.add(Restrictions.eq("toLocationId", id));
		if (includeSeen == false)
			criteria.add(Restrictions.eq("seen", false));
		List<RaxaAlert> alerts = new ArrayList<RaxaAlert>();
		alerts.addAll(criteria.list());
		return alerts;
	}
}
