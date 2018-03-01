package com.soleng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ExampleSeleniumTest {
	private WebDriver driver;
	private String baseUrl;
	private String contextroot;
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void setUp() throws Exception {
		String PROXY = System.getProperty("proxy");
		if (PROXY != null) {
			org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
			proxy.setHttpProxy(PROXY).setFtpProxy(PROXY).setSslProxy(PROXY);
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(CapabilityType.PROXY, proxy);
			driver = new FirefoxDriver(cap);
		} else {
			driver = new FirefoxDriver();
		}

		baseUrl = System.getProperty("baseurl");
		if (baseUrl == null) {
	           baseUrl = "https://140.86.32.144/AlphaProducts/viewrecords";
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@After
	public void tearDown() throws Exception {
		if (driver != null)
			this.driver.quit();
	}

	@Test
	public void testPageConnection() throws Exception {
		this.driver.get(baseUrl);
		assertEquals("Hello World", this.driver.getTitle());
	}

	@Test
	public void testCrayolaMarkersFound() throws Exception {
		// setup
		System.out.println("Starting test " + new Object() {
		}.getClass().getEnclosingMethod().getName());
		driver.get(baseUrl);

		// execute
		List<WebElement> elements = driver.findElements(By.xpath(".//*[@id='products']/h4"));
		boolean found = false;
		for (WebElement element : elements) {
			System.out.println("element:  " + element.getText());
			if (element.getText().contains("Crayola Original Markers - Broad Line, Classic Colors")) {
				found = true;
				break;
			}
		}

		// assert
		assertTrue(found);
	}
}
