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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.User;
import org.openmrs.Provider;
import org.openmrs.Person;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;
import org.raxa.module.raxacore.db.DrugGroupDAO;

/*
 * Implements PatientListService.java Note the PatientList query must be in the
 * form of:
 * "?encounterType=<uuid>&startDate=2012-05-07&endDate=2012-05-08&inlist=<uuidForList>&notinlist=<uuidForList>"
 */
public class DrugGroupServiceImpl implements DrugGroupService {
	
	private DrugGroupDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.raxa.module.raxacore.PatientListService#setPatientListDAO
	 */
	@Override
	public void setDrugGroupDAO(DrugGroupDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.raxa.module.raxacore.PatientListService#savePatientList
	 */
	@Override
	public DrugGroup saveDrugGroup(DrugGroup drugGroup) {
		return dao.saveDrugGroup(drugGroup);
	}
	
	/**
	 * @see org.raxa.module.raxacore.PatientListService#getPatientList(Integer)
	 */
	@Override
	public DrugGroup getDrugGroup(Integer id) {
		return dao.getDrugGroup(id);
	}
	
	/**
	 * @see
	 * org.raxa.module.raxacore.PatientListService#getPatientListByName(String)
	 */
	@Override
	public List<DrugGroup> getDrugGroupByName(String name) {
		return dao.getDrugGroupByName(name);
	}
	
	/**
	 * @see
	 * org.raxa.module.raxacore.PatientListService#getPatientListByUuid(String)
	 */
	@Override
	public DrugGroup getDrugGroupByUuid(String uuid) {
		return dao.getDrugGroupByUuid(uuid);
	}
	
	/**
	 * @see org.raxa.module.raxacore.PatientListService#getAllPatientList
	 */
	@Override
	public List<DrugGroup> getAllDrugGroup(boolean includeRetired) {
		return dao.getAllDrugGroup(includeRetired);
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
	 * @see org.raxa.module.raxacore.PatientListService#updatePatientList
	 */
	@Override
	public DrugGroup updateDrugGroup(DrugGroup drugGroup) {
		return dao.updateDrugGroup(drugGroup);
	}
	
	/**
	 * @see org.raxa.module.raxacore.PatientListService#deletePatientList
	 */
	@Override
	public void deleteDrugGroup(DrugGroup drugGroup) {
		dao.deleteDrugGroup(drugGroup);
	}
	
	@Override
	public void onStartup() {
		log.info("Starting patient list service");
	}
	
	@Override
	public void onShutdown() {
		log.info("Stopping patient list service");
	}
}
