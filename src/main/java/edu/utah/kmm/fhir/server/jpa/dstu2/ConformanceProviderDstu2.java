package edu.utah.kmm.fhir.server.jpa.dstu2;


import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.jpa.starter.AppProperties;
import ca.uhn.fhir.jpa.starter.BaseJpaRestfulServer;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import edu.utah.kmm.fhir.server.jpa.ServerUtil;

import javax.servlet.http.HttpServletRequest;

import static edu.utah.kmm.fhir.server.jpa.ServerUtil.SERVER_BASE_PLACEHOLDER;
import static edu.utah.kmm.fhir.server.jpa.ServerUtil.SMART_EXTENSION_URL;

public class ConformanceProviderDstu2 extends JpaConformanceProviderDstu2 {

	private final AppProperties appProperties;

	public ConformanceProviderDstu2(
		BaseJpaRestfulServer theRestfulServer,
		IFhirSystemDao<Bundle, MetaDt> theSystemDao,
		DaoConfig theDaoConfig) {
		super(theRestfulServer, theSystemDao, theDaoConfig);
		this.appProperties = theRestfulServer.getAppProperties();
	}

	public Conformance getServerConformance(
		HttpServletRequest theRequest,
		RequestDetails theRequestDetails) {
		Conformance cs = super.getServerConformance(theRequest, theRequestDetails);
		String oAuthBase = appProperties.getOauth_base();

		if (oAuthBase != null) {
			boolean hasServerBase = oAuthBase.contains(SERVER_BASE_PLACEHOLDER);
			String base = hasServerBase ? ServerUtil.resolveServerBase(oAuthBase, theRequest) : oAuthBase;
			ExtensionDt extSMART = new ExtensionDt().setUrl(SMART_EXTENSION_URL);
			cs.getRestFirstRep().getSecurity().addUndeclaredExtension(extSMART);
			ExtensionDt extAuth = new ExtensionDt().setUrl("authorize").setValue(new UriDt(base + "/authorize"));
			ExtensionDt extToken = new ExtensionDt().setUrl("token").setValue(new UriDt(base + "/token"));
			extSMART.addUndeclaredExtension(extAuth);
			extSMART.addUndeclaredExtension(extToken);
		}

        return cs;
    }

}
