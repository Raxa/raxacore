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
import org.raxa.module.raxacore.BillingItemAdjustment;

import org.raxa.module.raxacore.db.BillingItemAdjustmentDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateBillingItemAdjustmentDAO implements BillingItemAdjustmentDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public BillingItemAdjustment saveBillingItemAdjustment(BillingItemAdjustment adjustment) {
		sessionFactory.getCurrentSession().saveOrUpdate(adjustment);
		return adjustment;
	}
	
	@Transactional
	public void deleteBillingItemAdjustment(BillingItemAdjustment adjustment) {
		sessionFactory.getCurrentSession().delete(adjustment);
	}
	
	@Transactional
	public BillingItemAdjustment getBillingItemAdjustmentByUuid(String uuid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItemAdjustment.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		
		return (BillingItemAdjustment) criteria.uniqueResult();
	}
	
	@Transactional
	public BillingItemAdjustment getBillingItemAdjustment(int billItemAdjustmentId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItemAdjustment.class);
		criteria.add(Restrictions.eq("billItemAdjustmentId", billItemAdjustmentId));
		
		return (BillingItemAdjustment) criteria.uniqueResult();
	}
	
	@Transactional
	public List<BillingItemAdjustment> getAllBillingItemAdjustments() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItemAdjustment.class);
		return criteria.list();
	}
	
	@Transactional
	public List<BillingItemAdjustment> getAllBillingItemAdjustmentsByReason(String reason) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItemAdjustment.class);
		criteria.add(Restrictions.eq("reason", reason));
		List<BillingItemAdjustment> bills = new ArrayList<BillingItemAdjustment>();
		bills.addAll(criteria.list());
		return bills;
	}
	
	@Transactional
	public BillingItemAdjustment updateBillingItemAdjustment(BillingItemAdjustment adjustment) {
		sessionFactory.getCurrentSession().update(adjustment);
		return adjustment;
	}
	
	@Transactional
	public List<BillingItemAdjustment> getAllBillingItemAdjustmentsByBillingItem(Integer billingitemid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItemAdjustment.class);
		criteria.add(Restrictions.eq("billItemId", billingitemid));
		List<BillingItemAdjustment> bills = new ArrayList<BillingItemAdjustment>();
		bills.addAll(criteria.list());
		return bills;
	}
}
