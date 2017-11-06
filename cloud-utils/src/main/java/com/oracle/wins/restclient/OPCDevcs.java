package com.oracle.wins.restclient;

import java.util.ArrayList;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import com.oracle.wins.util.OPCProperties;

public class OPCDevcs {

	public static String deleteDevcsProject(String devcsOrg, String projectIdentifier) {

		System.out.println("DevCS project to delete: " + devcsOrg);

		String sUri = "developer." + OPCProperties.getInstance().getProperty(OPCProperties.OPC_REGION)
				+ ".oraclecloud.com/" + devcsOrg + "-"
				+ OPCProperties.getInstance().getProperty(OPCProperties.OPC_IDENTITY_DOMAIN) + "/api/projects/"
				+ projectIdentifier;
		String response = ApacheHttpClientDelete.httpClientDELETE2(
				OPCProperties.getInstance().getProperty(OPCProperties.OPC_USERNAME),
				OPCProperties.getInstance().getProperty(OPCProperties.OPC_PASSWORD), sUri,
				OPCProperties.CONTENT_TYPE_JSON);

		System.out.println("Output from Server .... \n");
		System.out.println(response);
		return response;
	}

	public static String[] listDevcsProjects(String devcsOrg) {

		ArrayList<String> projects = new ArrayList<String>();
		OPCProperties opcProperties = OPCProperties.getInstance();

		// https://developer.us2.oraclecloud.com/#{devCSORG}-#{identityDomain}/api/projects/search
		String sBaseUrl = "developer." + opcProperties.getProperty(OPCProperties.OPC_REGION) + ".oraclecloud.com";

		String sRestUrl = "/" + devcsOrg + "-" + opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN)
				+ "/api/projects/search";

		StringEntity seBody = new StringEntity(
				"{\"projectRelationship\":\"ALL\",\"organizationIdentifier\":\"" + devcsOrg + "-"
						+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN) + "\",\"shallowCopy\":true}",
				ContentType.DEFAULT_TEXT);

		String response = ApacheHttpClientPost.httpClientPOST2(opcProperties.getProperty(OPCProperties.OPC_USERNAME),
				opcProperties.getProperty(OPCProperties.OPC_PASSWORD), sBaseUrl, sRestUrl,
				OPCProperties.CONTENT_TYPE_JSON, seBody);

		JsonArray projectlist = Json.parse(response).asObject().get("queryResult").asObject().get("resultPage")
				.asArray();

		for (JsonValue jsonValue : projectlist) {
			projects.add(jsonValue.asObject().get("identifier").asString());
		}

		System.out.println("DevCS Projects: " + projects);

		return projects.toArray(new String[0]);
	}

	public static void main(String[] args) {

		ArrayList<String> projects = new ArrayList<String>();
		String response = "{\"queryResult\":{\"offset\":0,\"totalResultSize\":1,\"pageSize\":0,\"resultPage\":[{\"id\":19714,\"identifier\":\"developer45690-gse00012288_twitter-feed-marketing-project-gde_19714\",\"name\":\"Twitter Feed Marketing Project - Gde\",\"description\":\"Project to gather and analyze twitter data\",\"accessibility\":\"PRIVATE\",\"projectPreferences\":null,\"projectServices\":null,\"numWatchers\":0,\"numCommiters\":1,\"organization\":null,\"readOnly\":false,\"language\":\"en\",\"isLocked\":false,\"features\":null,\"state\":null,\"urlId\":null,\"currentUserRelation\":null,\"template\":false}]}}";

		JsonArray projectlist = Json.parse(response).asObject().get("queryResult").asObject().get("resultPage")
				.asArray();

		for (JsonValue jsonValue : projectlist) {
			projects.add(jsonValue.asObject().get("name").asString());
		}

		System.out.println("DevCS Projects: " + projects);
	}

}
