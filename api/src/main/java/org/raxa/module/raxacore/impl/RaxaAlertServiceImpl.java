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
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
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
	public List<RaxaAlert> getRaxaAlertsByName(String name, boolean includeSeen) {
		return dao.getRaxaAlertByName(name, includeSeen);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByAlertType(String)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByAlertType(String alertType, boolean includeSeen) {
		return (List<RaxaAlert>) dao.getRaxaAlertByAlertType(alertType, includeSeen);
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
	public List<RaxaAlert> getRaxaAlertByPatientId(Integer patientId, boolean includeSeen) {
		return dao.getRaxaAlertByPatientId(patientId, includeSeen);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByProviderRecipientId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderRecipientId(Integer providerRecipientId, boolean includeSeen) {
		return dao.getRaxaAlertByProviderRecipientId(providerRecipientId, includeSeen);
	}
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#getRaxaAlertByProviderSentId(Integer)
	 */
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderSentId(Integer providerSentId, boolean includeSeen) {
		return dao.getRaxaAlertByProviderSentId(providerSentId, includeSeen);
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
	
	/**
	 * This executes on startup
	 */
	@Override
	public void onStartup() {
		log.info("Starting raxa alert service");
	}
	
	/**
	 * This executes on shutdown
	 */
	@Override
	public void onShutdown() {
		log.info("Stopping raxa alert service");
	}
	
	@Override
	public void voidRaxaAlert(RaxaAlert raxaAlert, String reason) {
		dao.voidRaxaAlert(raxaAlert, reason);
	}
	
	@Override
	public void purgeRaxaAlert(RaxaAlert raxaAlert) {
		dao.deleteRaxaAlert(raxaAlert);
	}
	
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderSentUuid(String providerSentUuid, boolean includeSeen) {
		Provider p = Context.getProviderService().getProviderByUuid(providerSentUuid);
		if (p == null) {
			return new ArrayList<RaxaAlert>();
		}
		return dao.getRaxaAlertByProviderSentId(p.getId(), includeSeen);
	}
	
	@Override
	public List<RaxaAlert> getRaxaAlertByProviderRecipientUuid(String providerRecipientUuid, boolean includeSeen) {
		return dao.getRaxaAlertByProviderRecipientId(Context.getProviderService().getProviderByUuid(providerRecipientUuid)
		        .getId(), includeSeen);
	}
	
	@Override
	public List<RaxaAlert> getRaxaAlertByToLocationUuid(String toLocation, boolean includeSeen) {
		return dao.getRaxaAlertByToLocationId(Context.getLocationService().getLocationByUuid(toLocation).getId(),
		    includeSeen);
	}
	
}
