package edu.utah.kmm.fhir.server.jpa.dstu2;


import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import edu.utah.kmm.fhir.server.jpa.ServerUtil;

import javax.servlet.http.HttpServletRequest;

import static edu.utah.kmm.fhir.server.jpa.ServerUtil.SERVER_BASE_PLACEHOLDER;
import static edu.utah.kmm.fhir.server.jpa.ServerUtil.SMART_EXTENSION_URL;

public class ConformanceProviderDstu2 extends JpaConformanceProviderDstu2 {

    private final String oAuthBase;

    private final boolean hasServerBase;

    public ConformanceProviderDstu2(String oAuthBase, RestfulServer theRestfulServer, IFhirSystemDao<Bundle, MetaDt> theSystemDao, DaoConfig theDaoConfig) {
        super(theRestfulServer, theSystemDao, theDaoConfig);
        this.oAuthBase = oAuthBase;
        hasServerBase = oAuthBase != null && oAuthBase.contains(SERVER_BASE_PLACEHOLDER);
    }

    public Conformance getServerConformance(HttpServletRequest theRequest, RequestDetails theRequestDetails) {
        Conformance cs = super.getServerConformance(theRequest, theRequestDetails);

        if (oAuthBase != null) {
            String base = hasServerBase ? ServerUtil.resolveServerBase(oAuthBase, theRequest) : oAuthBase;
            ExtensionDt extSMART = new ExtensionDt().setUrl(SMART_EXTENSION_URL);
            cs.getRestFirstRep().getSecurity().addUndeclaredExtension(extSMART);
            ExtensionDt extAuth = new ExtensionDt().setUrl("authorize").setValue(new UriDt(base + "authorize"));
            ExtensionDt extToken = new ExtensionDt().setUrl("token").setValue(new UriDt(base + "token"));
            extSMART.addUndeclaredExtension(extAuth);
            extSMART.addUndeclaredExtension(extToken);
        }

        return cs;
    }

}
