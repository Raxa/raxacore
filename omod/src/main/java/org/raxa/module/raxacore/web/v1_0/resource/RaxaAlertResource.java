package org.raxa.module.raxacore.web.v1_0.resource;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Provider;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.raxa.module.raxacore.RaxaAlert;
import org.raxa.module.raxacore.RaxaAlertService;

/**
 * {@link Resource} for {@link Location}, supporting standard CRUD operations
 */
@Resource("raxaalert")
@Handler(supports = RaxaAlert.class, order = 0)
public class RaxaAlertResource extends DataDelegatingCrudResource<RaxaAlert> {
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("patient", Representation.REF);
			description.addProperty("seen");
			description.addProperty("alertType");
			description.addProperty("time");
			description.addProperty("defaultTask");
			description.addProperty("providerSent", Representation.REF);
			description.addProperty("providerRecipient", Representation.REF);
			//cannot add 'voided' property as a bug exists in OpenMRS: tickets.openmrs.org/browse/TRUNK-2205
			//description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("patient");
			description.addProperty("seen");
			description.addProperty("alertType");
			description.addProperty("time");
			description.addProperty("defaultTask");
			description.addProperty("providerSent");
			description.addProperty("providerRecipient");
			//cannot add 'voided' property as a bug exists in OpenMRS: tickets.openmrs.org/browse/TRUNK-2205
			//description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("patient");
		description.addRequiredProperty("providerSent");
		description.addRequiredProperty("providerRecipient");
		
		description.addProperty("alertType");
		description.addProperty("name");
		description.addProperty("time");
		description.addProperty("defaultTask");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = getCreatableProperties();
		description.addProperty("seen");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(org.openmrs.Encounter)
	 */
	@Override
	public RaxaAlert save(RaxaAlert raxaAlert) {
		return Context.getService(RaxaAlertService.class).saveRaxaAlert(raxaAlert);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public RaxaAlert getByUniqueId(String uuid) {
		return Context.getService(RaxaAlertService.class).getRaxaAlertByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#doGetAll()
	 * @param context
	 * @return
	 * @throws ResponseException 
	 */
	@Override
	protected NeedsPaging<RaxaAlert> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<RaxaAlert>(Context.getService(RaxaAlertService.class).getAllRaxaAlerts(true), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(org.openmrs.Encounter,
	 * java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(RaxaAlert raxaAlert, String reason, RequestContext context) throws ResponseException {
		if (raxaAlert.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getService(RaxaAlertService.class).voidRaxaAlert(raxaAlert, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(org.openmrs.Encounter,
	 * org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(RaxaAlert raxaAlert, RequestContext context) throws ResponseException {
		if (raxaAlert == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getService(RaxaAlertService.class).purgeRaxaAlert(raxaAlert);
	}
	
	/**
	 * @param raxaAlert
	 * @return encounter type and date
	 */
	public String getDisplayString(RaxaAlert raxaAlert) {
		String ret = "To:";
		ret += raxaAlert.getProviderRecipient() == null ? "?" : raxaAlert.getProviderRecipient().getPerson().getPersonName()
		        .toString();
		ret += " ";
		ret += raxaAlert.getName() == null ? "?" : raxaAlert.getName();
		ret += " ";
		ret += raxaAlert.getTime() == null ? "?" : Context.getDateFormat().format(raxaAlert.getTime());
		return ret;
	}
	
	/**
	 * Gets RaxaAlerts for the given provider (paged according to context if necessary)
	 *
	 * @param providerUniqueId
	 * @see {@link PatientResource#getByUniqueId(String)} for interpretation
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	public SimpleObject getRaxaAlertsByProvider(String providerUniqueId, RequestContext context) throws ResponseException {
		Provider provider = Context.getProviderService().getProviderByUuid(providerUniqueId);
		if (provider == null) {
			throw new ObjectNotFoundException();
		}
		List<RaxaAlert> raxaAlerts = Context.getService(RaxaAlertService.class).getRaxaAlertByProviderRecipientId(
		    provider.getId(), false);
		return new NeedsPaging<RaxaAlert>(raxaAlerts, context).toSimpleObject();
	}
	
	@PropertyGetter("providerSent")
	public static Object getProviderSent(RaxaAlert instance) {
		return Context.getProviderService().getProvider(instance.getProviderSentId());
	}
	
	@PropertySetter("providerSent")
	public static void setProviderSent(RaxaAlert instance, Provider p) {
		instance.setProviderSent(p);
		instance.setProviderSentId(p.getId());
	}
	
	/**
	 * Required for DataDelegatingCrudResource-- we don't have any delegates
	 * @return 
	 */
	@Override
	public RaxaAlert newDelegate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
