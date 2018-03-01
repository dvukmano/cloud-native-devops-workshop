package com.soleng;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class ExampleIntegrationTest {

	@Test
	public void getDeployedUrlStatus() throws Exception {
		// setup
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String host = System.getProperty("HOST");
		if(host == null){
			host = "140.86.32.144";
		}
		HttpHost target = new HttpHost(host, 80, "http");
		
		Builder customBuilder = RequestConfig.custom();
		
		String proxy_host = System.getProperty("PROXY_HOST");
		String proxy_port = System.getProperty("PROXY_PORT");
		
		if(proxy_host != null && proxy_port != null){
			HttpHost proxy = new HttpHost(proxy_host, Integer.parseInt(proxy_port));
			customBuilder.setProxy(proxy);
			System.out.println("proxy " + proxy);
		}
		RequestConfig build = customBuilder.build();
        
		// When
		HttpGet request = new HttpGet("/AlphaProducts/viewrecords");
		request.setConfig(build);
		
		System.out.println("Executing request " + request.getRequestLine() + " to " + target);

        CloseableHttpResponse response = httpclient.execute(target, request);

		//execute and assert
		assertEquals(response.getStatusLine().getStatusCode(), 200);
	}
}
