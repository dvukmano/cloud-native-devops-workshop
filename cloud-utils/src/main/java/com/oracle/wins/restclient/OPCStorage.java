package com.oracle.wins.restclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.oracle.wins.util.OPCProperties;

public class OPCStorage {
	
	public static String getStorageInformation() {
		
		OPCProperties opcProperties = OPCProperties.getInstance();
		
		Properties storageProperties = getStorageAuthToken(
				opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD),
				opcProperties.getProperty(OPCProperties.OPC_STORAGE_GENERIC_URL),
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));
    	
    	BasicNameValuePair[] aHeaders = null;
		String sUri = null;	
    	
    	sUri = storageProperties.getProperty(OPCProperties.HEADER_X_STORAGE_URL);
    	
    	aHeaders = new BasicNameValuePair[1];
    	
    	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));

		
		return ApacheHttpClientGet.httpClientGET(sUri, aHeaders, null, null);
		
	}
	
	public static String createStorage() {
		
		OPCProperties opcProperties = OPCProperties.getInstance();
		Properties storageProperties = getStorageAuthToken(
				opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD),
				opcProperties.getProperty(OPCProperties.OPC_STORAGE_GENERIC_URL),
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));
		
    	BasicNameValuePair[] aHeaders = null;
		String sUri = null;	

    	sUri = storageProperties.getProperty(OPCProperties.HEADER_X_STORAGE_URL) + "/" + opcProperties.getProperty(OPCProperties.OPC_STORAGE_CONTAINER);
    	
    	aHeaders = new BasicNameValuePair[1];
    	
    	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));
    	
    	return ApacheHttpClientPut.httpClientPUT(sUri, aHeaders, null, null, true);
	}
	
	
	public static String deleteStorage (String containerName) {
		
		System.out.println("Delete container:" + containerName);
		OPCProperties opcProperties = OPCProperties.getInstance();
		long tokenTimeStart = System.currentTimeMillis();

    	Properties storageProperties = getStorageAuthToken(
				opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD),
				opcProperties.getProperty(OPCProperties.OPC_STORAGE_GENERIC_URL),
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

    	String sUri = storageProperties.getProperty(OPCProperties.HEADER_X_STORAGE_URL);

    	BasicNameValuePair[] aHeaders = new BasicNameValuePair[1];

    	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));

    	//list objects in container into file
    	String response = ApacheHttpClientGet.httpClientGET(sUri + "/" + containerName, aHeaders, null, null);

    	boolean needDelete = true;

    	if(response.indexOf("Not Found") > -1) {
    		System.out.println("Not found container: " + containerName);
    		needDelete = false;
    	} else if (response.indexOf("Executon failed") > -1) {
    		System.out.println("Execution failed for container: " + containerName);
    		needDelete = false;
    	} else if (response.indexOf("No Content") == -1) {

			aHeaders = new BasicNameValuePair[1];
        	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));
    		
    		//delete objects 1 by 1
			String[] lines = response.split("\n");
			System.out.println("Number of objects to delete: " + lines.length);
			for (int i = 0; i < lines.length; i++) {
				
				if (System.currentTimeMillis() - tokenTimeStart > 600000) {
					System.out.println("Authentication token for storage will expire. Getting new one.");
					
					tokenTimeStart = System.currentTimeMillis();
		        	storageProperties = getStorageAuthToken(
		    				opcProperties.getProperty(OPCProperties.OPC_USERNAME),
		    				opcProperties.getProperty(OPCProperties.OPC_PASSWORD),
		    				opcProperties.getProperty(OPCProperties.OPC_STORAGE_GENERIC_URL),
		    				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));
					aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));
				}
				
				System.out.println("Object: " + lines[i]);
				response = ApacheHttpClientDelete.httpClientDELETE(
						sUri + "/"  + containerName + "/" + lines[i], aHeaders, null, null, null, true);
	            if ((lines.length - i) % 100 == 0) {
	                System.out.println("Number of objects to delete: " + (lines.length - i));
	            }
			}

    	}

    	//delete container
    	if (needDelete) {
        	aHeaders = new BasicNameValuePair[1];

        	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));

        	response = ApacheHttpClientDelete.httpClientDELETE(sUri + "/" + containerName, aHeaders, null, null, null);

        	System.out.println("Output from Server .... \n");
    		System.out.println(response);
    	}
    	return null;
	}
	
	
	public static String deleteStorageBulkVersion (String containerName) {
		
		System.out.println("Delete container:" + containerName);
		
		OPCProperties opcProperties = OPCProperties.getInstance();

    	Properties storageProperties = getStorageAuthToken(
				opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD),
				opcProperties.getProperty(OPCProperties.OPC_STORAGE_GENERIC_URL),
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

    	String sUri = storageProperties.getProperty(OPCProperties.HEADER_X_STORAGE_URL);

    	BasicNameValuePair[] aHeaders = new BasicNameValuePair[1];

    	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));

    	//list objects in container into file
    	String response = ApacheHttpClientGet.httpClientGET(sUri + "/" + containerName, aHeaders, null, null);

    	boolean needDelete = true;

    	if(response.indexOf("Not Found") > -1) {
    		System.out.println("Not found container: " + containerName);
    		needDelete = false;
    	} else if (response.indexOf("Executon failed") > -1) {
    		System.out.println("Execution failed for container: " + containerName);
    		needDelete = false;
    	} else if (response.indexOf("No Content") == -1) {

    		response = response.replace("\n", "\n" + containerName + "/");
			response = containerName + "/" + response;
			response = response.substring(0, response.lastIndexOf(containerName));
			
			//System.out.println("===============================================================================");
			//System.out.println(response);
			//System.out.println("===============================================================================");
			
			aHeaders = new BasicNameValuePair[2];

        	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));
        	aHeaders[1] = new BasicNameValuePair("Content-Type", OPCProperties.CONTENT_TYPE_TEXT);

        	StringEntity seBody = null;
        	try {
				seBody = new StringEntity(response);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

        	//delete objects in the container
        	response = ApacheHttpClientDelete.httpClientDELETE(sUri + "?bulk-delete", aHeaders, seBody, null, null);
        	System.out.println(response);
    	}

    	//delete container
    	if (needDelete) {
        	aHeaders = new BasicNameValuePair[1];

        	aHeaders[0] = new BasicNameValuePair("X-Auth-Token", storageProperties.getProperty(OPCProperties.HEADER_X_AUTH_TOKEN));

        	response = ApacheHttpClientDelete.httpClientDELETE(sUri + "/" + containerName, aHeaders, null, null, null);

        	System.out.println("Output from Server .... \n");
    		System.out.println(response);
    	}
    	return null;
	}
	

	
	public static Properties getStorageAuthToken(String sUsername, String sPassword,
			String sStorageUrl, String sIdentityDomain) {

		Properties storageProp = new Properties();

		try {

			CloseableHttpClient httpclient = HttpClients.custom().build();
			HttpGet httpget = new HttpGet("https://" + sIdentityDomain + "." + sStorageUrl + "/auth/v1.0");
			httpget.addHeader("X-Storage-User", "Storage-" + sIdentityDomain + ":" + sUsername);
			httpget.addHeader("X-Storage-Pass", sPassword);

			System.out.println("Executing request " + httpget.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httpget);

			System.out.println("Response: " + response.getStatusLine());

			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("FAILED check the error : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			Header[] headers = response.getAllHeaders();

			for (Header header: headers) {

				if (header.getName().contains(OPCProperties.HEADER_X_AUTH_TOKEN)) {
					System.out.println("auth token:" + header.getValue());
					storageProp.setProperty(OPCProperties.HEADER_X_AUTH_TOKEN, header.getValue());
				} else if (header.getName().contains(OPCProperties.HEADER_X_STORAGE_URL)) {
					System.out.println("storage url:" + header.getValue());
					storageProp.setProperty(OPCProperties.HEADER_X_STORAGE_URL, header.getValue());
				}
			}

			response.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return storageProp;
	}
}
