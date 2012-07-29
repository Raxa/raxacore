/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.web.v1_0.resource;

import java.util.List;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
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
 * {@link Resource} for DrugGroup, supporting standard CRUD operations
 * @author Yan
 */

@Resource("druggroup")
@Handler(supports = DrugGroup.class, order = 0)
public class DrugGroupResource extends MetadataDelegatingCrudResource<DrugGroup> {
	
	private DrugGroupService getDrugGroupService() {
		return Context.getService(DrugGroupService.class);
	}
	
	@PropertyGetter("druggroup")
	public List<DrugGroup> getAllDrugGroupList() {
		return getDrugGroupService().getDrugGroupList();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			//		description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("drug_group_name");
			//		description.addProperty("description");
			//		description.addProperty("searchQuery");
			//			description.addProperty("druggroup", Representation.REF);
			//		description.addProperty("retired");
			//		description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			//		description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("drug_group_name");
			//		description.addProperty("description");
			//		description.addProperty("searchQuery");
			//		description.addProperty("druggroup", Representation.DEFAULT);
			//		description.addProperty("retired");
			//		description.addProperty("auditInfo", findMethod("getAuditInfo"));
			//		description.addSelfLink();
			return description;
		}
		return null;
		
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		//		description.addRequiredProperty("description");
		//		description.addProperty("searchQuery");
		return description;
	}
	
	/*	@Override
		public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
	                description.addRequiredProperty("name");
	//		description.addProperty("description");
	//		description.addProperty("searchQuery");
			return description;
		}
	  */
	@Override
	public DrugGroup newDelegate() {
		return new DrugGroup();
	}
	
	@Override
	public DrugGroup save(DrugGroup drugGroup) {
		return getDrugGroupService().saveDrugGroup(drugGroup);
	}
	
	@Override
	public DrugGroup getByUniqueId(String uuid) {
		return getDrugGroupService().getDrugGroupByUuid(uuid);
	}
	
	@Override
	protected NeedsPaging<DrugGroup> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<DrugGroup>(getDrugGroupService().getDrugGroupList(), context);
	}
	
	@Override
	public void purge(DrugGroup t, RequestContext rc) throws ResponseException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
