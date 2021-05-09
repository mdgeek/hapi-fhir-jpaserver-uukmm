package edu.utah.kmm.fhir.server.jpa.dstu3;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.searchparam.registry.ISearchParamRegistry;
import ca.uhn.fhir.jpa.starter.AppProperties;
import ca.uhn.fhir.jpa.starter.BaseJpaRestfulServer;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import edu.utah.kmm.fhir.server.jpa.ServerUtil;
import org.hl7.fhir.dstu3.model.*;

import javax.servlet.http.HttpServletRequest;

import static edu.utah.kmm.fhir.server.jpa.ServerUtil.SERVER_BASE_PLACEHOLDER;
import static edu.utah.kmm.fhir.server.jpa.ServerUtil.SMART_EXTENSION_URL;

public class ConformanceProviderDstu3 extends JpaConformanceProviderDstu3 {

	private final AppProperties appProperties;

	public ConformanceProviderDstu3(
		BaseJpaRestfulServer theRestfulServer,
		IFhirSystemDao<Bundle, Meta> theSystemDao,
		DaoConfig theDaoConfig,
		ISearchParamRegistry theSearchParamRegistry) {
		super(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry);
		this.appProperties = theRestfulServer.getAppProperties();
	}

	public CapabilityStatement getServerConformance(
		HttpServletRequest theRequest,
		RequestDetails theRequestDetails) {
		CapabilityStatement cs = super.getServerConformance(theRequest, theRequestDetails);
		String oAuthBase = appProperties.getOauth_base();

		if (oAuthBase != null) {
			boolean hasServerBase = oAuthBase.contains(SERVER_BASE_PLACEHOLDER);
			String base = hasServerBase ? ServerUtil.resolveServerBase(oAuthBase, theRequest) : oAuthBase;
			Extension ext = cs.getRestFirstRep().getSecurity().addExtension().setUrl(SMART_EXTENSION_URL);
			ext.addExtension().setUrl("authorize").setValue(new UriType(base + "/authorize"));
			ext.addExtension().setUrl("token").setValue(new UriType(base + "/token"));
		}

		return cs;
	}

}
