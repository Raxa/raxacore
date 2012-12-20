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
import org.openmrs.Encounter;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.Billing;
import org.raxa.module.raxacore.db.BillingDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateBillingDAO implements BillingDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional
	public Billing saveBill(Billing bill) throws DAOException {
		
		sessionFactory.getCurrentSession().saveOrUpdate(bill);
		return bill;
	}
	
	@Transactional
	public void deleteBill(Billing bill) throws DAOException {
		
		sessionFactory.getCurrentSession().delete(bill);
	}
	
	@Transactional
	public Billing getBillByPatientUuid(String uuid) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Billing.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		
		return (Billing) criteria.uniqueResult();
	}
	
	@Transactional
	public List<Billing> getAllBills() throws DAOException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Billing.class);
		return criteria.list();
		
	}
	
	@Transactional
	public List<Billing> getAllBillsByStatus(String status) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Billing.class);
		criteria.add(Restrictions.eq("status", status));
		List<Billing> bills = new ArrayList<Billing>();
		bills.addAll(criteria.list());
		return bills;
		
	}
	
	@Transactional
	public Billing updateBill(Billing bill) {
		
		sessionFactory.getCurrentSession().update(bill);
		return bill;
	}
	
	@Transactional
	public Billing getBill(int billId) throws DAOException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Billing.class);
		criteria.add(Restrictions.eq("billId", billId));
		
		return (Billing) criteria.uniqueResult();
		
	}
	
	@Transactional
	public List<Billing> getAllBillsByProvider(Integer providerId) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Billing.class);
		criteria.add(Restrictions.eq("providerId", providerId));
		List<Billing> bills = new ArrayList<Billing>();
		bills.addAll(criteria.list());
		return bills;
	}
	
	@Transactional
	public List<Billing> getAllBillsByPatient(Integer patientId) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Billing.class);
		criteria.add(Restrictions.eq("patientId", patientId));
		List<Billing> bills = new ArrayList<Billing>();
		bills.addAll(criteria.list());
		return bills;
	}
	
	@Transactional
	public List<Encounter> getEncountersByPatientId(Integer patientId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
		criteria.add(Restrictions.eq("patientId", patientId));
		List<Encounter> bills = new ArrayList<Encounter>();
		bills.addAll(criteria.list());
		return bills;
	}
	
}
