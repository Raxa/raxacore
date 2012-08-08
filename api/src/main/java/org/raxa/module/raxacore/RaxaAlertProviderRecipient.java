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

import java.io.Serializable;
import org.openmrs.BaseOpenmrsData;

public class RaxaAlertProviderRecipient extends BaseOpenmrsData implements Serializable {
	
	private Integer raxaAlertProviderRecipientId;
	
	private Integer providerRecipentId;
	
	public RaxaAlertProviderRecipient() {
	}
	
	@Override
	public Integer getId() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public void setId(Integer intgr) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * @return the raxaAlertProviderRecipientId
	 */
	public Integer getRaxaAlertProviderRecipientId() {
		return raxaAlertProviderRecipientId;
	}
	
	/**
	 * @param raxaAlertProviderRecipientId the raxaAlertProviderRecipientId to set
	 */
	public void setRaxaAlertProviderRecipientId(Integer raxaAlertProviderRecipientId) {
		this.raxaAlertProviderRecipientId = raxaAlertProviderRecipientId;
	}
	
	/**
	 * @return the providerRecipentId
	 */
	public Integer getProviderRecipentId() {
		return providerRecipentId;
	}
	
	/**
	 * @param providerRecipentId the providerRecipentId to set
	 */
	public void setProviderRecipentId(Integer providerRecipentId) {
		this.providerRecipentId = providerRecipentId;
	}
}
