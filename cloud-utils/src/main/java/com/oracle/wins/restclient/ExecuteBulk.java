package com.oracle.wins.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.concurrent.ThreadSafe;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.oracle.wins.util.OPCProperties;

@ThreadSafe
public class ExecuteBulk {

	public static String[][] readDomainList() {

		String line = "";
		String cvsSplitBy = ";";
		ArrayList<String[]> domains = new ArrayList<String[]>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				ExecuteBulk.class.getClassLoader().getResourceAsStream(OPCProperties.BULK_LIST), "UTF-8"))) {

			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] domain = line.split(cvsSplitBy);
				System.out.println("Domains [domain= " + domain[0] + " , datacenter=" + domain[1] + " , user="
						+ domain[2] + " , password=" + domain[3] + " , devcs=" + domain[4] + "]");
				domains.add(domain);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out
				.println("Domain list for bulk operation loaded -----------------------------------------------------");

		return domains.toArray(new String[0][0]);
	}

	public static String bulkDeleteACCS(String defaultPropertiesFile) {

		for (String[] domain : readDomainList()) {
			if (OPCProperties.testPropertiesFile("environment.properties." + domain[0])) {
				OPCProperties.getInstance().init("environment.properties." + domain[0]);
			} else {
				OPCProperties.getInstance().init(defaultPropertiesFile);
			}

			// set the current domain and credential
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_IDENTITY_DOMAIN, domain[0]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_REGION, domain[1]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_USERNAME, domain[2]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_PASSWORD, domain[3]);

			// List ACCS instances
			String[] ACCSInstances = getACCSInstanceNames();

			// Delete ACCS instances
			for (int i = 0; i < ACCSInstances.length; i++) {
				OPCAccs.deleteAccs(ACCSInstances[i]);
			}
		}

		System.out.println("Bulk ACCS delete complete -----------------------------------------------------");

		return "Bulk ACCS delete complete.";
	}

	public static String bulkDeleteDEVCS(String defaultPropertiesFile) {

		for (String[] domain : readDomainList()) {
			if (OPCProperties.testPropertiesFile("environment.properties." + domain[0])) {
				OPCProperties.getInstance().init("environment.properties." + domain[0]);
			} else {
				OPCProperties.getInstance().init(defaultPropertiesFile);
			}

			// set the current domain and credential
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_IDENTITY_DOMAIN, domain[0]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_REGION, domain[1]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_USERNAME, domain[2]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_PASSWORD, domain[3]);

			// List DEVCS projects
			String[] projects = OPCDevcs.listDevcsProjects(domain[4]);

			// Delete DEVCS projects
			for (int i = 0; i < projects.length; i++) {
				OPCDevcs.deleteDevcsProject(domain[4], projects[i]);
			}

		}

		return "Bulk DEVCS delete complete.";
	}

	public static String bulkDeleteStorage(String defaultPropertiesFile) {

		for (String[] domain : readDomainList()) {
			if (OPCProperties.testPropertiesFile("environment.properties." + domain[0])) {
				OPCProperties.getInstance().init("environment.properties." + domain[0]);
			} else {
				OPCProperties.getInstance().init(defaultPropertiesFile);
			}

			// set the current domain and credential
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_IDENTITY_DOMAIN, domain[0]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_REGION, domain[1]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_USERNAME, domain[2]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_PASSWORD, domain[3]);

			ArrayList<String> containerList = new ArrayList<String>();
			
			// List Storage containers
			String containers = OPCStorage.getStorageInformation();
			
			if (containers.indexOf("No Content") == -1 && containers.indexOf("204") == -1) {
				containerList = new ArrayList<String>(Arrays.asList(containers.split("\n")));
			}
			System.out.println("Storage list:" + containerList);
		
			// Delete Storage Containers
			for (String containerName : containerList) {
				if (!containerName.startsWith("_apaas")) {
					OPCStorage.deleteStorage(containerName);
				}
			}

		}

		return "Bulk DEVCS delete complete.";
	}

	public static String bulkDeleteJCS(String defaultPropertiesFile) {

		for (String[] domain : readDomainList()) {
			if (OPCProperties.testPropertiesFile("environment.properties." + domain[0])) {
				OPCProperties.getInstance().init("environment.properties." + domain[0]);
			} else {
				OPCProperties.getInstance().init(defaultPropertiesFile);
			}

			// set the current domain and credential
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_IDENTITY_DOMAIN, domain[0]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_REGION, domain[1]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_USERNAME, domain[2]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_PASSWORD, domain[3]);

			// List JCS instances
			String[] JCSInstances = getJCSInstanceNames();

			// Delete JCS instances
			for (int i = 0; i < JCSInstances.length; i++) {
				OPCJava.deleteJcs(JCSInstances[i]);
			}
		}

		return "Bulk JCS delete complete -----------------------------------------------------";

	}

	public static String bulkDeleteDBCS(String defaultPropertiesFile) {

		for (String[] domain : readDomainList()) {
			if (OPCProperties.testPropertiesFile("environment.properties." + domain[0])) {
				OPCProperties.getInstance().init("environment.properties." + domain[0]);
			} else {
				OPCProperties.getInstance().init(defaultPropertiesFile);
			}

			// set the current domain and credential
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_IDENTITY_DOMAIN, domain[0]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_REGION, domain[1]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_USERNAME, domain[2]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_PASSWORD, domain[3]);

			// List DBCS instances
			String[] DBCSInstances = getDBCSInstanceNames();

			// Delete DBCS instances
			for (int i = 0; i < DBCSInstances.length; i++) {
				OPCDatabase.deleteDbcs(DBCSInstances[i]);
			}
		}

		return "Bulk DBCS delete complete -----------------------------------------------------";

	}
	
	public static String bulkCreateDBCS(String defaultPropertiesFile) {

		for (String[] domain : readDomainList()) {
			if (OPCProperties.testPropertiesFile("environment.properties." + domain[0])) {
				OPCProperties.getInstance().init("environment.properties." + domain[0]);
			} else {
				OPCProperties.getInstance().init(defaultPropertiesFile);
			}

			// set the current domain and credential
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_IDENTITY_DOMAIN, domain[0]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_REGION, domain[1]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_USERNAME, domain[2]);
			OPCProperties.getInstance().setProperty(OPCProperties.OPC_PASSWORD, domain[3]);

			String result = "";
			int iResult = ExecuteBatch.isStorageContainerExist();

			if ( iResult == OPCProperties.EXIST) {
				return "Can not create storage. Reason: " + OPCProperties.getInstance().getProperty(OPCProperties.OPC_STORAGE_CONTAINER) + " container already exists.";
			} else if (iResult == OPCProperties.FAILED) {
				return "Can not create storage. Reason: Request failed.";
			}
			
			iResult = ExecuteBatch.isDBCSExist();
			if ( iResult == OPCProperties.EXIST) {
				return "Can not create Database Cloud Service. Reason: " + OPCProperties.getInstance().getProperty(OPCProperties.DBCS_INSTANCE_1) + " instance already exists.";
			} else if (iResult == OPCProperties.FAILED) {
				return "Can not create Database Cloud Service. Reason: Request failed.";
			}
			
			OPCStorage.createStorage();
			while ((result = OPCStorage.getStorageInformation()) == null || result.indexOf(OPCProperties.getInstance().getProperty(OPCProperties.OPC_STORAGE_CONTAINER)) == -1) {
				System.out.println("Waiting for storage container creation...");
				System.out.println(result);
			}
			System.out.println("Storage has been created. -----------------------------------------------------");

			OPCDatabase.createDBCSInstance();
			
			System.out.println("Database creation request was accepted. -----------------------------------------------------");
			
		}

		return "Bulk DBCS create complete -----------------------------------------------------";

	}	

	public static String[] getJCSInstanceNames() {

		System.out.println("List JCS Instances");

		ArrayList<String> listOfJcs = new ArrayList<String>();
		String response = OPCJava.listJCS();

		System.out.println("List JCS Instances: " + response);
		
		JsonObject object = Json.parse(response).asObject();
		JsonArray services = object.get("services").asArray();

		for (JsonValue jsonValue : services) {
			listOfJcs.add(jsonValue.asObject().get("service_name").asString());
		}

		System.out.println("Existing JCS services: " + listOfJcs);

		return listOfJcs.toArray(new String[0]);
	}

	public static String[] getDBCSInstanceNames() {

		System.out.println("List DBCS Instances");

		ArrayList<String> listOfDbcs = new ArrayList<String>();
		String response = OPCDatabase.listDBCS();
		
		System.out.println("List DBCS Instances: " + response);

		JsonObject object = Json.parse(response).asObject();
		JsonArray services = object.get("services").asArray();

		for (JsonValue jsonValue : services) {
			listOfDbcs.add(jsonValue.asObject().get("service_name").asString());
		}

		System.out.println("Existing DBCS services: " + listOfDbcs);

		return listOfDbcs.toArray(new String[0]);
	}

	public static String[] getACCSInstanceNames() {

		ArrayList<String> listOfAccs = new ArrayList<String>();
		System.out.println("List ACCS Instances");

		String response = OPCAccs.listAccs();

		JsonObject object = Json.parse(response).asObject();

		JsonArray applications = object.get("applications").asArray();

		for (JsonValue jsonValue : applications) {
			listOfAccs.add(jsonValue.asObject().get("name").asString());
		}

		System.out.println("Existing ACCS services: " + listOfAccs);

		return listOfAccs.toArray(new String[0]);
	}

}