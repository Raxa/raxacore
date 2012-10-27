package org.raxa.module.raxacore.impl;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.User;
import org.openmrs.Provider;
import org.openmrs.Person;
import org.openmrs.Patient;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.DrugInfoService;
import org.raxa.module.raxacore.db.DrugInfoDAO;

public class DrugInfoServiceImpl implements DrugInfoService {
	
	private DrugInfoDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.raxa.module.raxacore.DrugInfoService#setDrugInfoDAO
	 */
	@Override
	public void setDrugInfoDAO(DrugInfoDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.raxa.module.raxacore.DrugInfoService#saveDrugInfo
	 */
	@Override
	public DrugInfo saveDrugInfo(DrugInfo drugInfo) {
		return dao.saveDrugInfo(drugInfo);
	}
	
	/**
	 * @see org.raxa.module.raxacore.DrugInfoService#getDrugInfo(Integer)
	 */
	@Override
	public DrugInfo getDrugInfo(Integer id) {
		return dao.getDrugInfo(id);
	}
	
	/**
	 * @see
	 * org.raxa.module.raxacore.DrugInfoService#getDrugInfoByUuid(String)
	 */
	@Override
	public DrugInfo getDrugInfoByUuid(String uuid) {
		return dao.getDrugInfoByUuid(uuid);
	}
	
	@Override
	public DrugInfo getDrugInfoByDrugUuid(String uuid) {
		Drug d = Context.getConceptService().getDrugByUuid(uuid);
		return dao.getDrugInfoByDrug(d.getId());
	}
	
	@Override
	public List<DrugInfo> getDrugInfosByDrugName(String name) {
		List<Drug> drugs = Context.getConceptService().getDrugs(name);
		List<DrugInfo> drugInfos = new ArrayList<DrugInfo>();
		for (int i = 0; i < drugs.size(); i++) {
			drugInfos.add(dao.getDrugInfoByDrug(drugs.get(i).getDrugId()));
		}
		return drugInfos;
	}
	
	/**
	 * @see org.raxa.module.raxacore.DrugInfoService#getAllDrugInfo
	 */
	@Override
	public List<DrugInfo> getAllDrugInfo(boolean includeVoided) {
		return dao.getAllDrugInfo(includeVoided);
	}
	
	/**
	 * Parses a string into a date
	 *
	 * @param str String to be parsed (must be iso format)
	 * @return Date
	 */
	private Date getDateFromString(String str) {
		
		String[] supportedFormats = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ssZ",
		        "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
		for (int i = 0; i < supportedFormats.length; i++) {
			try {
				Date date = new SimpleDateFormat(supportedFormats[i]).parse(str);
				return date;
			}
			catch (Exception ex) {
				//log.error(ex.getMessage() + " Error parsing string " + str + " into Date");
			}
		}
		log.error("Date string is malformed");
		return null;
	}
	
	/**
	 * @see org.raxa.module.raxacore.DrugInfoService#updateDrugInfo
	 */
	@Override
	public DrugInfo updateDrugInfo(DrugInfo drugInfo) {
		return dao.updateDrugInfo(drugInfo);
	}
	
	/**
	 * @see org.raxa.module.raxacore.DrugInfoService#deleteDrugInfo
	 */
	@Override
	public void deleteDrugInfo(DrugInfo drugInfo) {
		dao.deleteDrugInfo(drugInfo);
	}
	
	@Override
	public void onStartup() {
		log.info("Starting drug info service");
	}
	
	@Override
	public void onShutdown() {
		log.info("Stopping drug info service");
	}
}
