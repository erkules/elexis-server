package es.fhir.rest.core.resources;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ch.elexis.core.findings.util.fhir.IFhirTransformer;
import ch.elexis.core.findings.util.fhir.IFhirTransformerRegistry;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import es.fhir.rest.core.resources.util.IContactSearchFilterQueryAdapter;
import es.fhir.rest.core.resources.util.QueryUtil;

@Component
public class OrganizationResourceProvider implements IFhirResourceProvider {
	
	private Logger log;
	private ResourceProviderUtil resourceProviderUtil;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService modelService;
	
	@Reference
	private IFhirTransformerRegistry transformerRegistry;
	
	@Override
	public Class<? extends IBaseResource> getResourceType(){
		return Organization.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IFhirTransformer<Organization, IOrganization> getTransformer(){
		return (IFhirTransformer<Organization, IOrganization>) transformerRegistry
			.getTransformerFor(Organization.class, IOrganization.class);
	}
	
	@Activate
	public void activate(){
		log = LoggerFactory.getLogger(getClass());
		resourceProviderUtil = new ResourceProviderUtil();
	}
	
	@Create
	public MethodOutcome create(@ResourceParam Organization patient){
		return resourceProviderUtil.createResource(getTransformer(), patient, log);
	}
	
	@Read
	public Organization read(@IdParam IdType theId){
		String idPart = theId.getIdPart();
		if (idPart != null) {
			Optional<IOrganization> organization = modelService.load(idPart, IOrganization.class);
			if (organization.isPresent()) {
				Optional<Organization> fhirOrganization =
					getTransformer().getFhirObject(organization.get());
				return fhirOrganization.get();
				
			}
		}
		return null;
	}
	
	@Update
	public MethodOutcome update(@IdParam IdType theId, @ResourceParam Organization patient){
		// TODO request lock or fail
		return resourceProviderUtil.updateResource(theId, getTransformer(), patient, log);
	}
	
	@Delete
	public void delete(@IdParam IdType theId){
		// TODO request lock or fail
		if (theId != null) {
			Optional<IOrganization> resource =
				modelService.load(theId.getIdPart(), IOrganization.class);
			if (!resource.isPresent()) {
				throw new ResourceNotFoundException(theId);
			}
			modelService.delete(resource.get());
		}
	}
	
	@Search
	public List<Organization> search(@OptionalParam(name = Organization.SP_NAME) StringParam name,
		@OptionalParam(name = ca.uhn.fhir.rest.api.Constants.PARAM_FILTER) StringAndListParam theFtFilter){
		
		IQuery<IOrganization> query = modelService.getQuery(IOrganization.class);
		
		if (name != null) {
			QueryUtil.andContactNameCriterion(query, name);
		}
		
		if (theFtFilter != null) {
			new IContactSearchFilterQueryAdapter().adapt(query, theFtFilter);
		}
		
		List<IOrganization> organizations = query.execute();
		List<Organization> _organizations =
			organizations.parallelStream().map(org -> getTransformer().getFhirObject(org))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		return _organizations;
	}
	
}
