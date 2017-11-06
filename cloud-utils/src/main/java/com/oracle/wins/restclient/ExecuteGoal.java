package com.oracle.wins.restclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;

import com.oracle.wins.keygen.JSCHKeyGenerator;
import com.oracle.wins.util.OPCProperties;

public class ExecuteGoal {

	public static void main(String[] args) {

		OPCProperties opcProperties;
		String response = "";
		String sGoal = "";
		String sJobId = "";

		if (args.length < 2) {
			System.out.println("Not enough parameters defined!");
		} else {
			System.out.println("Selected goal: " + args[1]);

			sGoal = args[1];
			
			opcProperties = OPCProperties.getInstance();
			//if BULK operation then properties will be loaded later one-by-one
			if (!sGoal.toLowerCase().startsWith("bulk")) {
				opcProperties.init(args[0]);
			}

			if (args.length > 2 && args[2] != null) {
				sJobId = args[2];
			}

			Credentials credOPCUser = null;
			BasicNameValuePair[] aHeaders = null;
			String sUri = null;

			switch (sGoal.toLowerCase()) {
	        case OPCProperties.GOAL_JCS_GET_INSTANCE_DETAILS:
	        	System.out.println("JCS get specific instance details----------------------------------------");

	        	response = OPCJava.getJCSInstanceDetail();

	        	System.out.println("Output from Server .... \n");
	    		System.out.println(response);

	        	break;
	        case OPCProperties.GOAL_JCS_GET_IP_ADDRESS:
	        	System.out.println("JCS get public IP address----------------------------------------");

	        	response = OPCJava.getJCSInstanceDetail();
	        	
	        	
	        	if (response.indexOf(OPCProperties.WLS_ADMIN_URL) > -1) {
		        	Pattern pattern = Pattern.compile(OPCProperties.IPADDRESS_PATTERN);
		        	Matcher matcher = pattern.matcher(response.substring(response.indexOf(OPCProperties.WLS_ADMIN_URL)));
		        	if (matcher.find()) {
		        	    response = "Public IP address of the JCS instance: " + matcher.group();
		        	} else{
		        	    response = "Can not extract IP address. See the full details:\n" + response;
		        	}
	        	} else {
	        		response = "Can not extract IP address. See the instance details:\n" + response;
	        	}
	        	
	    		System.out.println(response);

	        	break;	        	
	        case OPCProperties.GOAL_DBCS_GET_INSTANCE_DETAILS:
	        	System.out.println("DBCS get specific instance details----------------------------------------");

        		response = OPCDatabase.getDBCSInstanceDetail();

	        	System.out.println("Output from Server .... \n");
	    		System.out.println(response);

	            break;
	        case OPCProperties.GOAL_DBCS_GET_IP_ADDRESS:
	        	System.out.println("DBCS get public IP address----------------------------------------");

	        	response = OPCDatabase.getDBCSInstanceDetail();
	        	
	        	if (response.indexOf(OPCProperties.DB_CONNECT_DESCRIPTOR) > -1) {
		        	Pattern pattern2 = Pattern.compile(OPCProperties.IPADDRESS_PATTERN);
		        	Matcher matcher2 = pattern2.matcher(response.substring(response.indexOf(OPCProperties.DB_CONNECT_DESCRIPTOR)));
		        	if (matcher2.find()) {
		        	    response = "Public IP address of the DBCS instance: " + matcher2.group();
		        	} else{
		        		response = "Can not extract IP address. See the instance details:\n" + response;
		        	}
	        	} else {
	        		response = "Can not extract IP address. See the instance details:\n" + response;
	        	}
	        		
	        	
	    		System.out.println(response);

	        	break;	            
	        case OPCProperties.GOAL_JCS_INSTANCE_DELETE:
	        	System.out.println("JCS delete instance----------------------------------------");

	        	OPCJava.deleteJcs(opcProperties.getProperty(opcProperties.getProperty(OPCProperties.JCS_INSTANCE_1)));

	            break;
	        case OPCProperties.GOAL_DBCS_INSTANCE_DELETE:
	        	System.out.println("DBCS delete instance----------------------------------------");

	        	OPCDatabase.deleteDbcs(opcProperties.getProperty(OPCProperties.DBCS_INSTANCE_1));
	        	
	        	break;
	        case OPCProperties.GOAL_DBCS_INSTANCE_CREATE:
	        	System.out.println("DBCS create instance----------------------------------------");

	    		response = OPCDatabase.createDBCSInstance();

	    		System.out.println("Response.... \n");
	    		System.out.println(response);
	            break;
	        case OPCProperties.GOAL_JCS_INSTANCE_CREATE:
	        	System.out.println("JCS create instance----------------------------------------");

	        	response = OPCJava.createJCSInstance();

	    		System.out.println("Output from Server .... \n");
	    		System.out.println(response);
	            break;
	        case OPCProperties.GOAL_JCS_GET_SPECIFIC_JOB_DETAILS:
	        	System.out.println("JCS get specific instance creation details----------------------------------------");

	        	aHeaders = new BasicNameValuePair[2];

	        	aHeaders[0] = new BasicNameValuePair("accept", OPCProperties.CONTENT_TYPE_JSON);
	        	aHeaders[1] = new BasicNameValuePair("X-ID-TENANT-NAME", opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN));

				sUri = "http://" + opcProperties.getProperty(OPCProperties.OPC_BASE_URL)
						+ opcProperties.getProperty(OPCProperties.JCS_REST_URL)
						+ opcProperties.getProperty(OPCProperties.OPC_IDENTITY_DOMAIN)
						+ "/" + "status/create/job/" + sJobId;

				credOPCUser = new UsernamePasswordCredentials(opcProperties.getProperty(OPCProperties.OPC_USERNAME), opcProperties.getProperty(OPCProperties.OPC_PASSWORD));

	    		response = ApacheHttpClientGet.httpClientGET(sUri, aHeaders, credOPCUser, OPCProperties
						.getInstance().getProperty(OPCProperties.OPC_BASE_URL));

	    		System.out.println("Output from Server .... \n");
	    		System.out.println(response);
	            break;
	        case OPCProperties.GOAL_STORAGE_CREATE:
	        	System.out.println("Create storage----------------------------------------");

	        	response = OPCStorage.createStorage();

	        	System.out.println("Output from Server .... \n");
	    		System.out.println(response);
	        	break;
	        case OPCProperties.GOAL_STORAGE_LIST:
	        	System.out.println("List storage endpoints----------------------------------------");

	        	response = OPCStorage.getStorageInformation();

	        	System.out.println("Output from Server .... \n");
	    		System.out.println(response);
	        	break;
	        case OPCProperties.GOAL_STORAGE_DELETE:
	        	System.out.println("Delete storage endpoints----------------------------------------");
	        	
	        	OPCStorage.deleteStorage(opcProperties.getProperty(OPCProperties.OPC_STORAGE_CONTAINER));
	        	
	        	break;
	        case OPCProperties.GOAL_CREATE_JCS_AUTO:
	        	System.out.println("Create JCS including Storage, DBCS ----------------------------------------");

	        	response = ExecuteBatch.createJCSAuto();
	        	System.out.println(response);

	        	break;
	        case OPCProperties.GOAL_BULK_ACCS_DELETE:
	        	System.out.println("Bulk ACCS delete ----------------------------------------");

	        	response = ExecuteBulk.bulkDeleteACCS(args[0]);
	        	System.out.println(response);

	        	break;
	        case OPCProperties.GOAL_BULK_JCS_DELETE:
	        	System.out.println("Bulk JCS delete ----------------------------------------");

	        	response = ExecuteBulk.bulkDeleteJCS(args[0]);
	        	System.out.println(response);

	        	break;
	        case OPCProperties.GOAL_BULK_DBCS_CREATE:
	        	System.out.println("Bulk DBCS create ----------------------------------------");

	        	response = ExecuteBulk.bulkCreateDBCS(args[0]);
	        	System.out.println(response);

	        	break;	        	
	        case OPCProperties.GOAL_BULK_DBCS_DELETE:
	        	System.out.println("Bulk DBCS delete ----------------------------------------");

	        	response = ExecuteBulk.bulkDeleteDBCS(args[0]);
	        	System.out.println(response);

	        	break;
	        case OPCProperties.GOAL_BULK_DBCS_INFO:
	        	System.out.println("Bulk DBCS info ----------------------------------------");

	        	response = ExecuteBulk.bulkDBCSinfo(args[0]);
	        	System.out.println(response);

	        	break;	        	
	        case OPCProperties.GOAL_BULK_DEVCS_DELETE:
	        	System.out.println("Bulk DEVCS delete ----------------------------------------");

	        	response = ExecuteBulk.bulkDeleteDEVCS(args[0]);
	        	System.out.println(response);

	        	break;
	        case OPCProperties.GOAL_BULK_STORAGE_DELETE:
	        	System.out.println("Bulk Stotage delete ----------------------------------------");

	        	response = ExecuteBulk.bulkDeleteStorage(args[0]);
	        	System.out.println(response);

	        	break;	   	        	
	        case OPCProperties.GOAL_GENERATE_SSH_KEYPAIR:
	        	System.out.println("Generate SSH key pair ----------------------------------------");
	        	System.out.println("WARNING! This will create new public and private key and overwrite the existing keypairs!");
	        	System.out.println("The current keypairs will be copied with date postfix.");

				String passphrase = opcProperties.getProperty(OPCProperties.SSH_PASSPHRASE);
	        	System.out.println("Passphrase used from properties file (" + args[0] + "): " + (passphrase == null || passphrase.length() == 0 ? "Passphrase NOT defined" : passphrase));
	        	System.out.println();

	        	String datePostfix =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

	        	URL url = opcProperties.getClass().getClassLoader().getResource(args[0]);
	        	Path pathWorkingdir;
				try {
					pathWorkingdir = Paths.get(url.toURI()).getParent();
				} catch (URISyntaxException e1) {
					System.out.println("Failure during determine working dir.");
					e1.printStackTrace();
					break;
				}

	        	System.out.println("Working dir: " + pathWorkingdir.toAbsolutePath() + File.separator);

	        	//backup existing private key
	        	File file = new File(pathWorkingdir + File.separator + "pk.openssh");
	        	File backupFile = new File(pathWorkingdir + File.separator + "pk.openssh." + datePostfix);

	        	file.renameTo(backupFile);

	        	System.out.println("Private key backup: " + "pk.openssh." + datePostfix);

	        	//backup public key from properties file to separate file
	        	FileInputStream in;
				try {
		        	in = new FileInputStream(new File(pathWorkingdir + File.separator + args[0]));
		        	Properties props = new Properties();
		        	props.load(in);
		        	in.close();

		        	PrintWriter out = new PrintWriter(pathWorkingdir + File.separator + "public.key." + datePostfix);

		        	out.print(props.get(OPCProperties.SSH_PUBLIC_KEY));
		        	out.close();

		        	System.out.println("Public key backup: " + "public.key." + datePostfix);

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Failure during private key backup.");
					break;
				}

				//generate new keypair
				response = JSCHKeyGenerator.generateKeys(pathWorkingdir + File.separator + "pk.openssh", opcProperties.getProperty(OPCProperties.SSH_PASSPHRASE));
				//System.out.println("Public key: " + response);

				//replace public key in properties file
				try {

					System.out.println("Replacing public key in property file:" + pathWorkingdir + File.separator + args[0]);
					List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(pathWorkingdir + File.separator + args[0]), StandardCharsets.UTF_8));

					for (int i = 0; i < fileContent.size(); i++) {
					    if (fileContent.get(i).startsWith(OPCProperties.SSH_PUBLIC_KEY)) {
					        fileContent.set(i, OPCProperties.SSH_PUBLIC_KEY + "=ssh-rsa " + response + " rsa-key-" + datePostfix);
					        break;
					    }
					}

					Files.write(Paths.get(pathWorkingdir + File.separator + args[0]), fileContent, StandardCharsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
					response = "Failure during public key update.";
				}


				response = "Keypair has been generated.";

	        	System.out.println(response);

	        	break;
	        default:
	        	System.out.println("Wrong goal specified: " + sGoal);
	            break;
			}
		}

	}


}