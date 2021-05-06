package edu.utah.kmm.fhir.server.jpa;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class ServerUtil {

    public static final String SMART_EXTENSION_URL = "http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris";

    public static long ONE_MONTH = 30L * 24L * 60L * 60L * 1000L;

    public static String SERVER_BASE_PLACEHOLDER = "${serverBase}";

    private ServerUtil() {
    }

    public static String resolveServerBase(
            String url,
            HttpServletRequest theRequest) {
        if (url.contains(SERVER_BASE_PLACEHOLDER)) {
            String requestUrl = theRequest.getRequestURL().toString();
            String servletContextPath = StringUtils.defaultString(theRequest.getContextPath());
            int i = requestUrl.indexOf(servletContextPath);
            String base = i < 0 ? requestUrl : requestUrl.substring(0, i + servletContextPath.length());
            url = url.replace(SERVER_BASE_PLACEHOLDER, base);
        }

        return url;
    }

}
