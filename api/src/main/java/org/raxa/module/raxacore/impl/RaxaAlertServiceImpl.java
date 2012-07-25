package org.raxa.module.raxacore.impl;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.raxa.module.raxacore.RaxaAlert;
import org.raxa.module.raxacore.RaxaAlertService;
import org.raxa.module.raxacore.db.RaxaAlertDAO;

/*
 * Implements RaxaAlertService.java
 */
public class RaxaAlertServiceImpl implements RaxaAlertService {
	
	private RaxaAlertDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#setRaxaAlertDAO
	 */
	@Override
	public void setRaxaAlertDAO(RaxaAlertDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#saveRaxaAlert
	 */
	@Override
	public RaxaAlert saveRaxaAlert(RaxaAlert raxaAlert) {
		return dao.saveRaxaAlert(raxaAlert);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlert(Integer)
	 */
	@Override
	public RaxaAlert getRaxaAlert(Integer id) {
		return dao.getRaxaAlert(id);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByName(String)
	 */
	@Override
	public RaxaAlert getRaxaAlertsByName(String name) {
		return dao.getRaxaAlertByName(name);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByAlertType(String)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByAlertType(String alertType) {
		return (List<RaxaAlert>) dao.getRaxaAlertByAlertType(alertType);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByUuid(String)
	 */
	@Override
	public RaxaAlert getRaxaAlertByUuid(String uuid) {
		return dao.getRaxaAlertByUuid(uuid);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByPatientId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByPatientId(Integer patientId) {
		return dao.getRaxaAlertByPatientId(patientId);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByProviderRecipientId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderRecipientId(Integer providerRecipientId) {
		return dao.getRaxaAlertByProviderRecipientId(providerRecipientId);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByProviderSentId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderSentId(Integer providerSentId) {
		return dao.getRaxaAlertByProviderSentId(providerSentId);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getAllRaxaAlert
	 */
	@Override
	public List<RaxaAlert> getAllRaxaAlerts(boolean includeSeen) {
		return dao.getAllRaxaAlerts(includeSeen);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#markRaxaAlertAsSeen
	 */
	@Override
	public RaxaAlert markRaxaAlertAsSeen(RaxaAlert raxaAlert) {
		return dao.markRaxaAlertAsSeen(raxaAlert);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#updateRaxaAlert
	 */
	@Override
	public RaxaAlert updateRaxaAlert(RaxaAlert raxaAlert) {
		return dao.updateRaxaAlert(raxaAlert);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#deleteRaxaAlert
	 */
	@Override
	public void deleteRaxaAlert(RaxaAlert raxaAlert) {
		dao.deleteRaxaAlert(raxaAlert);
	}
	
	@Override
	public void onStartup() {
		log.info("Starting raxa alert service");
	}
	
	@Override
	public void onShutdown() {
		log.info("Stopping raxa alert service");
	}
	
}
