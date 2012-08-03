package org.raxa.module.raxacore.db;

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
import org.openmrs.EncounterType;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.DrugInfo;

/**
 * Interface for accessing raxacore_drug_info
 */
public interface DrugInfoDAO {

    /**
     * Saves a DrugInfo
     *
     * @param DrugInfo to be saved
     * @throws DAOException @should save a patient list
     */
    public DrugInfo saveDrugInfo(DrugInfo drugInfo) throws DAOException;

    /**
     * Purge a DrugInfo from database.
     *
     * @param DrugInfo object to be purged
     */
    public void deleteDrugInfo(DrugInfo drugInfo) throws DAOException;

    /**
     * Get DrugInfo by internal identifier
     *
     * @param drugInfoId patientList id
     * @return DrugInfo with given internal identifier
     * @throws DAOException @should get a patient list
     */
    public DrugInfo getDrugInfo(Integer drugInfoId) throws DAOException;

    /**
     * Find {@link DrugInfo} matching a uuid
     *
     * @param uuid
     * @return {@link DrugInfo} @should get a patient list by uuid
     */
    public DrugInfo getDrugInfoByUuid(String uuid);

    /**
     * Update DrugInfo
     *
     * @return {@link DrugInfo} @should update a DrugInfo
     */
    DrugInfo updateDrugInfo(DrugInfo drugInfo) throws DAOException;
}
