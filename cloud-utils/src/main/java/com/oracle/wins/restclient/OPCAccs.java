package com.oracle.wins.restclient;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;

import com.oracle.wins.util.OPCProperties;

public class OPCAccs {

	
	public static String listAccs() {

		OPCProperties opcProperties = OPCProperties.getInstance();
		BasicNameValuePair[] aHeaders = null;
		String sUri = null;

		aHeaders = new BasicNameValuePair[2];

    	aHeaders[0] = new BasicNameValuePair("accept", OPCProperties.CONTENT_TYPE_JSON);
    	aHeaders[1] = new BasicNameValuePair("X-ID-TENANT-NAME", opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

    	
    	//https://apaas.{region}.oraclecloud.com/paas/service/apaas/api/v1.1/apps/
		sUri = "https://apaas." + opcProperties.getProperty(OPCProperties.OPC_REGION) + ".oraclecloud.com"
				+ opcProperties.getProperty(OPCProperties.ACCS_REST_URL)
				+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN);

		Credentials credOPCUser = new UsernamePasswordCredentials(opcProperties.getProperty(OPCProperties.OPC_USERNAME), opcProperties.getProperty(OPCProperties.OPC_PASSWORD));

		return ApacheHttpClientGet.httpClientGET(sUri, aHeaders, credOPCUser, "apaas." + opcProperties.getProperty(OPCProperties.OPC_REGION) + ".oraclecloud.com");

	}
	
	public static String deleteAccs(String sApplicationName) {
    	System.out.println("ACCS delete instance: " + sApplicationName);

		Credentials credOPCUser = null;
		BasicNameValuePair[] aHeaders = null;
    	String sUri = "https://apaas." + OPCProperties.getInstance().getProperty(OPCProperties.OPC_REGION) + ".oraclecloud.com"
    			+ OPCProperties.getInstance().getProperty(OPCProperties.ACCS_REST_URL)
    			+ OPCProperties.getInstance().getProperty(OPCProperties.OPC_IDENTITY_DOMAIN)
    			+ "/" + sApplicationName;

    	credOPCUser = new UsernamePasswordCredentials(OPCProperties.getInstance().getProperty(OPCProperties.OPC_USERNAME), OPCProperties.getInstance().getProperty(OPCProperties.OPC_PASSWORD));

    	aHeaders = new BasicNameValuePair[2];

    	aHeaders[0] = new BasicNameValuePair("accept", OPCProperties.getInstance().getProperty(OPCProperties.CONTENT_TYPE_JSON));
    	aHeaders[1] = new BasicNameValuePair("X-ID-TENANT-NAME", OPCProperties.getInstance().getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));
    	
		String response = ApacheHttpClientDelete.httpClientDELETE(sUri, aHeaders, null, credOPCUser, "apaas." + OPCProperties.getInstance().getProperty(OPCProperties.OPC_REGION) + ".oraclecloud.com", false);

		System.out.println("Output from Server .... \n");
		System.out.println(response);
		return response;
	}

}
