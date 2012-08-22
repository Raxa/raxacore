package org.raxa.module.raxacore.web.v1_0.resource;

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
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.api.ConceptService;
import org.openmrs.Drug;

/**
 * {@link Resource} for Drug , supporting standard CRUD operations This resource
 * is currently not used because of serialization issue in OpenMRS core
 * (TRUNK-2205)
 */
@Resource("drug")
@Handler(supports = Drug.class, order = 0)
public class RaxaDrugResource extends MetadataDelegatingCrudResource<Drug> {
	
	private ConceptService getDrugService() {
		return Context.getConceptService();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("price");
			description.addProperty("cost");
			description.addProperty("description");
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("price");
			description.addProperty("cost");
			description.addProperty("retired");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("drug_id");
		description.addRequiredProperty("name");
		description.addRequiredProperty("description");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("description");
		return description;
	}
	
	@Override
	public Drug getByUniqueId(String uuid) {
		return getDrugService().getDrugByUuid(uuid);
	}
	
	@Override
	public Drug newDelegate() {
		return new Drug();
	}
	
	@Override
	public Drug save(Drug drugInfo) {
		return getDrugService().saveDrug(drugInfo);
	}
	
	@Override
	protected NeedsPaging<Drug> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Drug>(getDrugService().getAllDrugs(false), context);
	}
	
	@Override
	public String getDisplayString(Drug delegate) {
		if (delegate.getName() == null) {
			return "";
		}
		return delegate.getName() + " - " + delegate.getDescription();
	}
	
	@Override
	public void purge(Drug t, RequestContext rc) throws ResponseException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
