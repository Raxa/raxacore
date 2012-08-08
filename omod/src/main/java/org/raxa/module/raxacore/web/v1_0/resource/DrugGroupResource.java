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
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;

/**
 * {@link Resource} for DrugGroup, supporting standard CRUD operations This
 * resource is currently not used because of serialization issue in OpenMRS core
 * (TRUNK-2205)
 */
@Resource("druggroup")
@Handler(supports = DrugGroup.class, order = 0)
public class DrugGroupResource extends MetadataDelegatingCrudResource<DrugGroup> {
	
	/**
	 * Getter for the drugs property on patient list resource
	 *
	 * @param drugGroup
	 * @return
	 */
	@PropertyGetter("drugs")
	public List<Patient> getDrugs(DrugGroup drugGroup) {
		// return getDrugGroupService().getDrugsInDrugGroup(drugGroup);
		return null;
	}
	
	private DrugGroupService getDrugGroupService() {
		return Context.getService(DrugGroupService.class);
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("description");
			// description.addProperty("searchQuery");
			// description.addProperty("drugs", Representation.REF);
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
			// description.addProperty("searchQuery");
			// description.addProperty("drugs", Representation.DEFAULT);
			description.addProperty("retired");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty("description");
		// description.addProperty("searchQuery");
		return description;
	}
	
	/**
	 * @throws ResourceDoesNotSupportOperationException
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("description");
		// description.addProperty("searchQuery");
		return description;
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId()
	 */
	@Override
	public DrugGroup getByUniqueId(String uuid) {
		return getDrugGroupService().getDrugGroupByUuid(uuid);
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge()
	 */
	@Override
	public void purge(DrugGroup t, RequestContext rc) throws ResponseException {
		getDrugGroupService().deleteDrugGroup(t);
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public DrugGroup newDelegate() {
		return new DrugGroup();
	}
	
	/*
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save()
	 */
	@Override
	public DrugGroup save(DrugGroup drugGroup) {
		return getDrugGroupService().saveDrugGroup(drugGroup);
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#doGetAll()
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	@Override
	protected NeedsPaging<DrugGroup> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<DrugGroup>(getDrugGroupService().getAllDrugGroup(false), context);
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#doSearch()
	 * @param query
	 * @param context
	 * @return
	 */
	@Override
	protected NeedsPaging<DrugGroup> doSearch(String query, RequestContext context) {
		return new NeedsPaging<DrugGroup>(getDrugGroupService().getDrugGroupByName(query), context);
	}
	
	/**
	 * @see
	 * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getDisplayString()
	 * @param delegate
	 * @return
	 */
	@Override
	public String getDisplayString(DrugGroup delegate) {
		if (delegate.getName() == null) {
			return "";
		}
		return delegate.getName() + " - " + delegate.getDescription();
	}
}
