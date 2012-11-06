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
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.BillingItem;

import org.raxa.module.raxacore.db.BillingDAO;
import org.raxa.module.raxacore.db.BillingItemDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateBillingItemDAO implements BillingItemDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public BillingItem saveBillingItem(BillingItem item) {
		sessionFactory.getCurrentSession().saveOrUpdate(item);
		return item;
		
	}
	
	@Transactional
	public void deleteBillingItem(BillingItem item) {
		sessionFactory.getCurrentSession().delete(item);
	}
	
	@Transactional
	public BillingItem getBillingItemByUuid(String uuid)

	{
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		
		return (BillingItem) criteria.uniqueResult();
	}
	
	@Transactional
	public BillingItem getBillingItem(int billItemId)

	{
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("billItemId", billItemId));
		
		return (BillingItem) criteria.uniqueResult();
	}
	
	@Transactional
	public List<BillingItem> getAllBillingItems() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		return criteria.list();
	}
	
	@Transactional
	public List<BillingItem> getAllBillingItemsByBill(Integer billid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("billId", billid));
		List<BillingItem> bills = new ArrayList<BillingItem>();
		bills.addAll(criteria.list());
		return bills;
	}
	
	@Transactional
	public BillingItem updateBillingItem(BillingItem item) {
		sessionFactory.getCurrentSession().update(item);
		return item;
	}
	
	@Transactional
	public List<BillingItem> getAllBillingItemsByProvider(Integer providerId) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("providerId", providerId));
		List<BillingItem> bills = new ArrayList<BillingItem>();
		bills.addAll(criteria.list());
		return bills;
		
	}
	
	@Transactional
	public List<BillingItem> getAllBillingItemsByEncounter(Integer encounterId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("encounterId", encounterId));
		List<BillingItem> bills = new ArrayList<BillingItem>();
		bills.addAll(criteria.list());
		return bills;
	}
	
	@Transactional
	public List<BillingItem> getAllBillingItemsByConcept(Integer conceptId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("conceptId", conceptId));
		List<BillingItem> bills = new ArrayList<BillingItem>();
		bills.addAll(criteria.list());
		return bills;
	}
	
	@Transactional
	public List<BillingItem> getAllBillingItemsByOrder(Integer orderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BillingItem.class);
		criteria.add(Restrictions.eq("orderId", orderId));
		List<BillingItem> bills = new ArrayList<BillingItem>();
		bills.addAll(criteria.list());
		return bills;
	}
	
}
