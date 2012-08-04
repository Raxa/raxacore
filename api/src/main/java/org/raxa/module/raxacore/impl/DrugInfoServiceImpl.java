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
import org.raxa.module.raxacore.DrugInfo;
import org.raxa.module.raxacore.DrugInfoService;
import org.raxa.module.raxacore.db.DrugInfoDAO;

/*
 * Implements PatientListService.java Note the PatientList query must be in the
 * form of:
 * "?encounterType=<uuid>&startDate=2012-05-07&endDate=2012-05-08&inlist=<uuidForList>&notinlist=<uuidForList>"
 */
public class DrugInfoServiceImpl implements DrugInfoService {
	
	private DrugInfoDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.raxa.module.raxacore.PatientListService#setPatientListDAO
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
	 * @see org.raxa.module.raxacore.DrugInfoService#getDrugInfoByUuid(String)
	 */
	@Override
	public DrugInfo getDrugInfoByUuid(String uuid) {
		return dao.getDrugInfoByUuid(uuid);
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
	public void deleteDrugInfo(DrugInfo drugInf) {
		dao.deleteDrugInfo(drugInf);
	}
	
	@Override
	public void onStartup() {
		log.info("Starting DrugInfoService");
	}
	
	@Override
	public void onShutdown() {
		log.info("Stopping DrugInfoService");
	}
}
