package org.auscope.portal.core.services.methodmakers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.auscope.portal.core.server.OgcServiceProviderType;
import org.auscope.portal.core.services.methodmakers.CSWMethodMakerGetDataRecords.ResultType;
import org.auscope.portal.core.services.methodmakers.filter.csw.CSWGetDataRecordsFilter;
import org.auscope.portal.core.test.PortalTestClass;
import org.auscope.portal.core.util.DOMUtil;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Unit tests for CSWMethodMakerGetDataRecords
 * 
 * @author Josh Vote
 *
 */
public class TestCSWMethodMakerGetDataRecords extends PortalTestClass {

    private static final String uri = "http://test.com";

    private CSWMethodMakerGetDataRecords methodMaker;
    private CSWGetDataRecordsFilter mockFilter;

    /**
     * Setup each unit test
     */
    @Before
    public void init() {
        mockFilter = context.mock(CSWGetDataRecordsFilter.class);
        methodMaker = new CSWMethodMakerGetDataRecords();
    }

    /**
     * Simple test to ensure that makeMethod includes all appropriate fields
     * @throws IOException 
     * @throws IllegalStateException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test
    public void testMakeMethodFilter() throws IOException, ParserConfigurationException, SAXException {
        final int maxRecords = 1234;
        final String filterStr = "<filter/>";

        context.checking(new Expectations() {
            {
                allowing(mockFilter).getSortType();
                oneOf(mockFilter).getFilterStringAllRecords();
                will(returnValue(filterStr));
            }
        });

        HttpRequestBase method = methodMaker.makeMethod(uri, mockFilter, ResultType.Results, maxRecords, OgcServiceProviderType.Default);
        Assert.assertNotNull(method);

        Assert.assertTrue(method instanceof HttpPost); //we want this to be sent via post in case we get a large filter
        String postBody = IOUtils.toString(((HttpPost) method).getEntity().getContent(), StandardCharsets.UTF_8);

        Assert.assertTrue(postBody.contains(String.format("maxRecords=\"%1$s\"", maxRecords)));
        Assert.assertTrue(postBody.contains(String.format("resultType=\"results\"")));
        Assert.assertTrue(postBody.contains(filterStr));

        Assert.assertNotNull(DOMUtil.buildDomFromString(postBody));//this should NOT throw an exception
    }

    /**
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * Simple test to ensure that makeMethod includes all appropriate fields
     * @throws IOException 
     * @throws  
     */
    @Test
    public void testMakeMethodNoFilter() throws IOException, ParserConfigurationException, SAXException {
        final int maxRecords = 14;

        context.checking(new Expectations());

        HttpRequestBase method = methodMaker.makeMethod(uri, null, ResultType.Hits, maxRecords, OgcServiceProviderType.Default);
        Assert.assertNotNull(method);

        Assert.assertTrue(method instanceof HttpPost); //we want this to be sent via post in case we get a large filter

        String postBody = IOUtils.toString(((HttpPost) method).getEntity().getContent(), StandardCharsets.UTF_8);

        Assert.assertTrue(postBody.contains(String.format("maxRecords=\"%1$s\"", maxRecords)));
        Assert.assertTrue(postBody.contains(String.format("resultType=\"hits\"")));
        Assert.assertFalse(postBody.contains("csw:Constraint"));

        Assert.assertNotNull(DOMUtil.buildDomFromString(postBody));//this should NOT throw an exception
    }

    /**
     * @throws URISyntaxException 
     * Simple test to ensure that some of the 'mandatory' parameters are set correctly
     * @throws IOException 
     * @throws  
     */
    @Test
    public void testKeyParameters() throws  IOException, URISyntaxException {
        final int maxRecords = 1234;
        final String filterStr = "<filter/>";

        context.checking(new Expectations() {
            {
                allowing(mockFilter).getSortType();
                allowing(mockFilter).getFilterStringAllRecords();
                will(returnValue(filterStr));
            }
        });

        //Test POST
        HttpRequestBase method = methodMaker.makeMethod(uri, mockFilter, ResultType.Results, maxRecords, OgcServiceProviderType.Default);
        Assert.assertNotNull(method);
        String postBody = IOUtils.toString(((HttpPost) method).getEntity().getContent(), StandardCharsets.UTF_8);
        Assert.assertTrue(postBody.contains(String.format("version=\"2.0.2\"")));
        Assert.assertTrue(postBody.contains(String.format("outputSchema=\"http://www.isotc211.org/2005/gmd\"")));
        // for OgcServiceProviderType.Default, typeNames is gmd:MD_Metadata
        // for OgcServiceProviderType.PyCSW, typeNames is csw:Record
        Assert.assertTrue(postBody.contains(String.format("typeNames=\"gmd:MD_Metadata\"")));

        //Test GET
        method = methodMaker.makeGetMethod(uri, ResultType.Results, maxRecords, 0, OgcServiceProviderType.Default);
        Assert.assertNotNull(method);
        String queryString = ((HttpGet) method).getURI().getQuery();
        Assert.assertTrue(queryString, queryString.contains("version=2.0.2"));
        Assert.assertTrue(queryString, queryString.contains("outputSchema=http://www.isotc211.org/2005/gmd"));
        Assert.assertTrue(queryString, queryString.contains("typeNames=gmd:MD_Metadata"));

    }
    
    /**
     * @throws IOException
     * @throws URISyntaxException
     * Simple test to validate schema for OgcServiceProviderType.PyCSW 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test
    public void testMakeMethodForPyCSW() throws  IOException, URISyntaxException, ParserConfigurationException, SAXException {
        final int maxRecords = 1234;
        final String filterStr = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" ><ogc:BBOX>" + 
        		" 	<ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>" + 
        		" 		<gml:Envelope srsName=\"WGS:84\">" + 
        		" 			<gml:lowerCorner>143 -44</gml:lowerCorner>" + 
        		" 			<gml:upperCorner>148 -39</gml:upperCorner>" + 
        		" 		</gml:Envelope>" + 
        		" 	</ogc:BBOX></ogc:Filter>";

        context.checking(new Expectations() {
            {
                allowing(mockFilter).getSortType();
                oneOf(mockFilter).getFilterStringAllRecords();
                will(returnValue(filterStr));
            }
        });

        //Test Post
        HttpRequestBase method = methodMaker.makeMethod(uri, mockFilter, ResultType.Results, maxRecords, OgcServiceProviderType.PyCSW);
        Assert.assertNotNull(method);

        Assert.assertTrue(method instanceof HttpPost); //we want this to be sent via post in case we get a large filter
        String postBody = IOUtils.toString(((HttpPost) method).getEntity().getContent(), StandardCharsets.UTF_8);

        Assert.assertTrue(postBody.contains("typeNames=\"csw:Record\""));
        // method decorateFilterString should replace the envelope srsName for PyCSW request
        Assert.assertFalse(postBody.contains("<gml:Envelope srsName=\"WGS:84\">"));
        Assert.assertTrue(postBody.contains("<gml:Envelope srsName=\"urn:ogc:def:crs:OGC:1.3:CRS84\">"));
        
        //test Get
        method = methodMaker.makeGetMethod(uri, ResultType.Results, maxRecords, 0, OgcServiceProviderType.PyCSW);
        Assert.assertNotNull(method);
        String queryString = ((HttpGet) method).getURI().getQuery();

        Assert.assertFalse(queryString, queryString.contains("constraint_language_version=1.1.0"));
    }
    
    /**
     * @throws IOException
     * @throws URISyntaxException
     * Simple test to validate schema for OgcServiceProviderType.GeoServer 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test
    public void testMakeMethodForGeoserver() throws  IOException, URISyntaxException, ParserConfigurationException, SAXException {
        final int maxRecords = 1234;
        final String filterStr = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" ><ogc:BBOX>" + 
        		" 	<ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>" + 
        		" 		<gml:Envelope srsName=\"WGS:84\">" + 
        		" 			<gml:lowerCorner>143 -44</gml:lowerCorner>" + 
        		" 			<gml:upperCorner>148 -39</gml:upperCorner>" + 
        		" 		</gml:Envelope>" + 
        		" 	</ogc:BBOX></ogc:Filter>";

        context.checking(new Expectations() {
            {
                allowing(mockFilter).getSortType();
                oneOf(mockFilter).getFilterStringAllRecords();
                will(returnValue(filterStr));
            }
        });

        //Test Post
        HttpRequestBase method = methodMaker.makeMethod(uri, mockFilter, ResultType.Results, maxRecords, OgcServiceProviderType.GeoServer);
        Assert.assertNotNull(method);

        Assert.assertTrue(method instanceof HttpPost); //we want this to be sent via post in case we get a large filter
        String postBody = IOUtils.toString(((HttpPost) method).getEntity().getContent(), StandardCharsets.UTF_8);

        Assert.assertTrue(postBody.contains("<csw:Query typeNames=\"gmd:MD_Metadata\""));
        // method decorateFilterString should replace the bounding box property name for Geoserver only!
        Assert.assertFalse(postBody.contains("<ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>"));
        Assert.assertTrue(postBody.contains("<ogc:PropertyName>BoundingBox</ogc:PropertyName>"));
        
    }
    
}
