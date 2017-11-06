package com.oracle.wins.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class ApacheHttpClientDelete {

	public static String httpClientDELETE(String sUri, BasicNameValuePair[] aHeaders, StringEntity seBody,
			Credentials credOPCUser, String sAuthScopeURL, boolean nolog) {

		StringBuffer sbOutput = new StringBuffer();
		try {
			CloseableHttpClient httpClient = null;
			if (credOPCUser != null) {
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(
						(sAuthScopeURL == null ? AuthScope.ANY : new AuthScope(sAuthScopeURL, 443)), credOPCUser);
				httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			} else {
				httpClient = HttpClients.custom().build();
			}

			if (!nolog) {
				System.out.println("URI: " + sUri);
			}
			HttpResponse response = null;
			if (seBody == null) {
				HttpDelete httpDelete = new HttpDelete(sUri);

				for (BasicNameValuePair header : aHeaders) {
					httpDelete.addHeader(header.getName(), header.getValue());
				}

				response = httpClient.execute(httpDelete);
			} else {
				HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(sUri);

				for (BasicNameValuePair header : aHeaders) {
					httpDeleteWithBody.addHeader(header.getName(), header.getValue());
				}

				httpDeleteWithBody.setEntity(seBody);

				response = httpClient.execute(httpDeleteWithBody);
			}
			if (!nolog) {
				System.out.println("HTTP response code: " + response.getStatusLine().getStatusCode());
			}

			if (sUri.toLowerCase().contains("storage")) {
				if (sUri.toLowerCase().contains("bulk-delete") && response.getStatusLine().getStatusCode() == 200) {
					System.out.println("Check the status : (HTTP " + response.getStatusLine().getStatusCode() + ") "
							+ "Reason: " + response.getStatusLine().getReasonPhrase());
				} else if (response.getStatusLine().getStatusCode() != 204) {
					System.out.println("FAILED check the error : (HTTP " + response.getStatusLine().getStatusCode()
							+ ") Reason: " + response.getStatusLine().getReasonPhrase() + ", Object: " + sUri);
				}
			} else if (response.getStatusLine().getStatusCode() != 202) {
				System.out.println(
						"FAILED check the error : (HTTP " + response.getStatusLine().getStatusCode()
								+ ") Reason: " + response.getStatusLine().getReasonPhrase());
			}

			if (response.getEntity() != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				String sTemp;
				while ((sTemp = br.readLine()) != null) {
					sbOutput.append(sTemp).append("\n");
				}
			}

			httpClient.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sbOutput.toString();
	}

	public static String httpClientDELETE(String sUri, BasicNameValuePair[] aHeaders, StringEntity seBody,
			Credentials credOPCUser, String sAuthScopeURL) {
		return httpClientDELETE(sUri, aHeaders, seBody, credOPCUser, sAuthScopeURL, false);
	}

	public static String httpClientDELETE2(String sUsername, String sPassword, String sUri, String sContentType) {

		StringBuffer sbOutput = new StringBuffer();
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			String encoding = Base64.getEncoder().encodeToString(new String(sUsername + ":" + sPassword).getBytes());

			System.out.println("https://" + sUri);

			HttpDelete httpDelete = new HttpDelete("https://" + sUri);
			httpDelete.setHeader("Authorization", "Basic " + encoding);
			httpDelete.addHeader("Accept", sContentType);

			System.out.println("executing request " + httpDelete.getRequestLine());

			HttpResponse response = httpClient.execute(httpDelete);

			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() != 202 && response.getStatusLine().getStatusCode() != 200) {
				System.out.println(
						"FAILED check the error : HTTP error code : " + response.getStatusLine().getStatusCode());
				System.out.println("Reason: " + response.getStatusLine().getReasonPhrase());
			}

			if (response.getEntity() != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				String sTemp;
				while ((sTemp = br.readLine()) != null) {
					sbOutput.append(sTemp).append("\n");
				}
			}

			httpClient.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sbOutput.toString();
	}

}
