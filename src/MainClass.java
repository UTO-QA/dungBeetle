import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

public class MainClass {

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		// initialize selenium driver selenium
		WebDriver driver = new FirefoxDriver();

		// initialize the htmlunit client
		WebClient webClient = new WebClient();

		// Output file
		PrintWriter out = new PrintWriter(new FileWriter("DegreeSearch.csv"));
		out.println("Website,Code");

		// base website
		String baseWebsite = "https://webapp4-qa.asu.edu/programs/t5/minorscertificates/undergrad/true";

		// level 1
		ArrayList<String> websiteArray = websiteCrawler(driver, baseWebsite);

		// level 1 + level 2
		ArrayList<String> MasterArray = new ArrayList<String>();
		for (String website : websiteArray) {
			MasterArray.addAll(websiteCrawler(driver, website));
		}

		// remove duplicates
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(MasterArray);
		MasterArray.clear();
		MasterArray.addAll(hs);

		// Stop all stupid warnings from clogging the console
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setRedirectEnabled(false);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				Level.OFF);

		for (String website : MasterArray) {
			out.println(website + ",");
			try {
				int code = webClient.getPage(website).getWebResponse()
						.getStatusCode();
				webClient.closeAllWindows();
				System.out.println("-->" + code);
				out.println(code);
			} catch (Exception e) {
				System.out.println("-->Weird Error");
				out.println("Weird Error");
			}
		}

		out.close();

		driver.quit();
	}

	private static ArrayList<String> websiteCrawler(WebDriver driver,
			String baseWebsite) {

		// Open Base website
		driver.get(baseWebsite);

		// Collect all the websites
		ArrayList<WebElement> websiteArray = (ArrayList<WebElement>) driver
				.findElements(By.tagName("a"));

		// Filter Websites
		ArrayList<String> newWebsiteArray = new ArrayList<String>();
		for (WebElement website : websiteArray) {
			try {
				String x = website.getAttribute("href");
				if (x == null || x.contains("javascript") || x.isEmpty()) {
					continue;
				}
				System.out.println(x);
				newWebsiteArray.add(x);
			} catch (Exception e) {
				System.out.println("Website Exception!");
			}

			HashSet<String> hs = new HashSet<String>();
			hs.addAll(newWebsiteArray);
			newWebsiteArray.clear();
			newWebsiteArray.addAll(hs);
		}
		return newWebsiteArray;
	}
}
