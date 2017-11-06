package com.oracle.wins.restclient;

import java.io.UnsupportedEncodingException;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.oracle.wins.util.OPCProperties;

public class OPCDatabase {

	public static String getDBCSInstanceDetail() {

		OPCProperties opcProperties = OPCProperties.getInstance();
		BasicNameValuePair[] aHeaders = null;
		String sUri = null;

		aHeaders = new BasicNameValuePair[2];

		aHeaders[0] = new BasicNameValuePair("accept", OPCProperties.CONTENT_TYPE_JSON);
		aHeaders[1] = new BasicNameValuePair("X-ID-TENANT-NAME",
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

		sUri = "http://" + opcProperties.getProperty(OPCProperties.OPC_BASE_URL)
				+ opcProperties.getProperty(OPCProperties.DBCS_REST_URL)
				+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN) + "/"
				+ opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_1);

		Credentials credOPCUser = new UsernamePasswordCredentials(opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD));

		return ApacheHttpClientGet.httpClientGET(sUri, aHeaders, credOPCUser,
				OPCProperties.getInstance().getProperty(OPCProperties.OPC_BASE_URL));
	}

	public static String createDBCSInstance() {

		OPCProperties opcProperties = OPCProperties.getInstance();

		StringBuffer sbTemp = new StringBuffer();
		sbTemp.append("{");
		sbTemp.append("  \"serviceName\": \"" + opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_1) + "\",");
		sbTemp.append("  \"version\": \"" + opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_VERSION_1) + "\",");
		sbTemp.append("  \"level\": \"PAAS\",");
		sbTemp.append("  \"edition\": \"EE\",");
		sbTemp.append("  \"subscriptionType\": \"MONTHLY\",");
		sbTemp.append("  \"description\": \"" + opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_DESC_1) + "\",");
		sbTemp.append("  \"shape\": \"oc3\",");
		sbTemp.append("  \"vmPublicKeyText\": \"" + opcProperties.getProperty(OPCProperties.SSH_PUBLIC_KEY) + "\",");
		sbTemp.append("  \"parameters\": [");
		sbTemp.append("    {");
		sbTemp.append("      \"type\": \"db\",");
		sbTemp.append("      \"usableStorage\": \""
				+ opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_USABLE_STORAGE_1) + "\",");
		sbTemp.append(
				"      \"adminPassword\": \"" + opcProperties.getProperty(OPCProperties.DBCS_DBA_PASSWORD) + "\",");
		sbTemp.append("      \"sid\": \"" + opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_SID_1) + "\",");
		sbTemp.append("      \"pdb\": \"" + opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_PDB1_1) + "\",");
		sbTemp.append("      \"failoverDatabase\": \"no\",");
		sbTemp.append("      \"backupDestination\": \"BOTH\",");
		sbTemp.append("      \"cloudStorageContainer\" : \"Storage-"
				+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN) + "/"
				+ opcProperties.getProperty(OPCProperties.OPC_STORAGE_CONTAINER) + "\",");
		sbTemp.append("      \"cloudStorageUser\": \"" + opcProperties.getProperty(OPCProperties.OPC_USERNAME) + "\",");
		sbTemp.append("      \"cloudStoragePwd\": \"" + opcProperties.getProperty(OPCProperties.OPC_PASSWORD) + "\"");
		sbTemp.append("    }],");
		if (opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_DEMOS_1) == null) {
			return OPCProperties.DBCS_INSTANCE_DEMOS_1
					+ " property is missing. Value can be 'yes' or 'no'. See environment.properties.sample";
		}
		sbTemp.append("  \"additionalParams\": {\"db_demo\": \""
				+ opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_DEMOS_1) + "\"} ");
		sbTemp.append("}");

		StringEntity seBody = null;
		try {
			seBody = new StringEntity(sbTemp.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to construct body: " + e.getMessage());
		}

		String response = ApacheHttpClientPost.httpClientPOST(opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD),
				opcProperties.getProperty(OPCProperties.DBCS_REST_URL),
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN),
				opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_1),
				opcProperties.getProperty(OPCProperties.DBCS_BASE_URL), OPCProperties.CONTENT_TYPE_JSON, seBody);

		return response;
	}

	public static String listDBCS() {

		OPCProperties opcProperties = OPCProperties.getInstance();
		BasicNameValuePair[] aHeaders = null;
		String sUri = null;

		aHeaders = new BasicNameValuePair[2];

		aHeaders[0] = new BasicNameValuePair("accept", OPCProperties.CONTENT_TYPE_JSON);
		aHeaders[1] = new BasicNameValuePair("X-ID-TENANT-NAME",
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

		sUri = "http://" + opcProperties.getProperty(OPCProperties.OPC_BASE_URL)
				+ opcProperties.getProperty(OPCProperties.DBCS_REST_URL)
				+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN);

		Credentials credOPCUser = new UsernamePasswordCredentials(opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD));

		return ApacheHttpClientGet.httpClientGET(sUri, aHeaders, credOPCUser,
				OPCProperties.getInstance().getProperty(OPCProperties.OPC_BASE_URL));

	}

	public static String deleteDbcs(String sServiceName) {
		System.out.println("DBCS instance to delete: " + sServiceName);
		OPCProperties opcProperties = OPCProperties.getInstance();

		Credentials credOPCUser = null;
		BasicNameValuePair[] aHeaders = null;
		String sUri = "https://" + opcProperties.getProperty(OPCProperties.DBCS_BASE_URL)
				+ opcProperties.getProperty(OPCProperties.DBCS_REST_URL)
				+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN) + "/"
				+ sServiceName;

		System.out.println(sUri);

		credOPCUser = new UsernamePasswordCredentials(opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD));

		aHeaders = new BasicNameValuePair[2];

		aHeaders[0] = new BasicNameValuePair("accept", opcProperties.getProperty(OPCProperties.CONTENT_TYPE_JSON));
		aHeaders[1] = new BasicNameValuePair("X-ID-TENANT-NAME",
				opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

		String response = ApacheHttpClientDelete.httpClientDELETE(sUri, aHeaders, null, credOPCUser, opcProperties.getProperty(OPCProperties.DBCS_BASE_URL));

		System.out.println("Output from Server .... \n");
		System.out.println(response);

		return response;
	}

}
