package org.raxa.module.raxacore;

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
import java.util.List;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.raxa.module.raxacore.db.DrugGroupDAO;
import org.springframework.transaction.annotation.Transactional;

/*
 * Interface for interacting with the PatientList
 */
@Transactional
public interface DrugGroupService extends OpenmrsService {
	
	/**
	 * Sets Patient List DAO
	 *
	 * @param dao
	 */
	public void setDrugGroupDAO(DrugGroupDAO dao);
	
	/**
	 * Saves PatientList
	 *
	 * @param drugGroup
	 * @return PatientList
	 */
	@Authorized( { "Add Patient Lists" })
	public DrugGroup saveDrugGroup(DrugGroup drugGroup);
	
	/**
	 * Gets a PatientList by Id
	 *
	 * @param id
	 * @return PatientLists
	 */
	@Authorized( { "View Patient Lists" })
	public DrugGroup getDrugGroup(Integer id);
	
	/**
	 * Gets a PatientList by Name
	 *
	 * @param name
	 * @return list of PatientLists
	 */
	@Authorized( { "View Patient Lists" })
	public List<DrugGroup> getDrugGroupByName(String name);
	
	/**
	 * Gets PatientList by uuid
	 *
	 * @param uuid
	 * @return PatientList
	 */
	@Authorized( { "View Patient Lists" })
	public DrugGroup getDrugGroupByUuid(String uuid);
	
	/**
	 * Gets all PatientLists
	 *
	 * @return list of PatientLists
	 */
	@Authorized( { "View Patient Lists" })
	public List<DrugGroup> getAllDrugGroup(boolean includeRetired);
	
	/**
	 * Updates PatientList
	 *
	 * @param patientList
	 * @return PatientList
	 */
	@Authorized( { "Edit Patient Lists" })
	DrugGroup updateDrugGroup(DrugGroup drugGroup);
	
	/**
	 * Deletes PatientList
	 *
	 * @param patientList
	 */
	@Authorized( { "Delete Patient Lists" })
	public void deleteDrugGroup(DrugGroup drugGroup);
}
