package org.auscope.portal.core.services;

import java.util.Date;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.server.http.HttpServiceCaller;
import org.auscope.portal.core.services.methodmakers.SOSMethodMaker;
import org.auscope.portal.core.services.methodmakers.filter.FilterBoundingBox;
import org.auscope.portal.core.services.responses.ows.OWSExceptionParser;
import org.auscope.portal.core.services.responses.sos.SOSResponse;

/**
 * An abstract base class containing common functionality for all Service classes
 * that intend to interact with a one or more Web Feature Services.
 *
 * @author Florence Tan
 */
public  class SOSService {

	protected HttpServiceCaller httpServiceCaller;
    protected SOSMethodMaker sosMethodMaker;

    /**
     * Creates a new instance of this class with the specified dependencies
     * @param httpServiceCaller Will be used for making requests
     * @param sosMethodMaker Will be used for generating WFS methods
     */
    public SOSService(HttpServiceCaller httpServiceCaller,
            SOSMethodMaker sosMethodMaker) {
        this.httpServiceCaller = httpServiceCaller;
        this.sosMethodMaker = sosMethodMaker;
    }
      
    
    /**
     * Utility method for choosing the correct SOS method to generate based on specified parameters
     * @param sosUrl [required] - the sensor observation service url
     * @param request - required, service type identifier (e.g. GetCapabilities or GetObservation) 
     * @param featureOfInterest- optional - pointer to a feature of interest for which observations are requested 
     * @param beginPosition - optional - start time period for which observations are requested 
     *                       			the time should conform to ISO format: YYYY-MM-DDTHH:mm:ss+HH. 
     * @param endPosition - optional  -	end time period(s) for which observations are requested 
     *                             		the time should conform to ISO format: YYYY-MM-DDTHH:mm:ss+HH.
     *                                - both beginPosition and endPosition must go in pair, if one exists, the other must exists                               
     * @param bbox - optional         -	FilterBoundingBox object -> convert to 52NorthSOS BBOX format :
     *                          		maxlat,minlon,minlat,maxlon(,srsURI) 
     *                             		srsURI format : "http://www.opengis.net/def/crs/EPSG/0/"+epsg code 							                          
     * @return HttpMethodBase object
     * @throws Exception
     */
    protected HttpMethodBase generateSOSRequest(String sosUrl, String request, String featureOfInterest, Date beginPosition, Date endPosition, FilterBoundingBox bbox) {    	
            return sosMethodMaker.makePostMethod(sosUrl, request, featureOfInterest, beginPosition, endPosition, bbox);
    }

    
    /**
     * Makes a GetObservation request, transform the response using transformer and returns the 
     * lot bundled in a SOSTransformedResopnse
     * @param method a SOS GetObservation request
     * @param transformer A transformer to work with the resulting SOS response
     * @param styleSheetParams Properties to apply to the transformer
     * @return
     * @throws PortalServiceException
     */
    protected SOSResponse getSOSResponse(HttpMethodBase method) throws PortalServiceException {
        try {
            //Make the request and parse the response
            String responseString = httpServiceCaller.getMethodResponseAsString(method);
            OWSExceptionParser.checkForExceptionResponse(responseString);

            return new SOSResponse(responseString, method);
        } catch (Exception ex) {
            throw new PortalServiceException(method, ex);
        }
    }
}