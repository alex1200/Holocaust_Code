package InterviewParser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeleniumScrapper
{
    public static void main(String[] args)
    {
        SeleniumScrapper scrapper = new SeleniumScrapper();
        scrapper.downloadPDF();
    }

    private WebDriver driver;
    private String baseUrl;
    private String catalogEntry = "";

    public SeleniumScrapper()
    {
        System.setProperty("webdriver.gecko.driver", "D:\\Holocaust\\libs\\geckodriver.exe");
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.dir", "D:\\Holocaust\\WebDriverDownloads");
        profile.setPreference("browser.download.folderList", 2);

        //Set Preference to not show file download confirmation dialogue using MIME types Of different file extension types.
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/pdf,application/x-pdf");

        profile.setPreference( "browser.download.manager.showWhenStarting", false );
        profile.setPreference( "pdfjs.disabled", true );

        driver = new FirefoxDriver(profile);
        baseUrl = "https://collections.ushmm.org/search/catalog/irn507310";
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public SeleniumScrapper(String filename)
    {
        int ind = filename.lastIndexOf(".");
        if( ind>=0 )
        {
            catalogEntry = new StringBuilder(filename).replace(ind, ind + 1, "*").toString();
        }
        System.setProperty("webdriver.chrome.driver", "H:\\workspace\\Holocaust\\libs\\chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://collections.ushmm.org/search/catalog/irn507310";
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public Map<String,String> run()
    {
        driver.get(baseUrl);
        driver.findElement(By.className("accordionButton")).click();
        driver.findElement(By.linkText(catalogEntry)).click();

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        Map<String, String> datapairmap = new HashMap<>();
        List<WebElement> datapairs = driver.findElements(By.className("datapair"));
        for(WebElement element : datapairs){
            try {
                WebElement key = element.findElement(By.tagName("dt"));
                WebElement value = element.findElement(By.tagName("dd"));
                datapairmap.put(key.getText(), value.getText());
            }
            catch (Exception e){
                System.out.println("NO PAIR FOUND");
            }
        }
        driver.close();
        return datapairmap;
    }

    private void downloadPDF(){


        driver.get(baseUrl);
        driver.findElement(By.className("accordionButton")).click();
        List<WebElement> elements = driver.findElements(By.className("col-md-6"));
        int index = 0;
        for(int i = 47; i < elements.size(); i++) {
            try {
                driver.findElements(By.className("col-md-6")).get(i).click();
                Thread.sleep(500);
                try{
                    driver.findElement(By.partialLinkText("_trs_en.pdf")).click();
                }
                catch (Exception e){
                    System.out.println("CANT CLICK PDF");
                }
                Thread.sleep(10000);
                driver.get(baseUrl);
//                driver.navigate().back();
                Thread.sleep(2000);
                driver.findElement(By.className("accordionButton")).click();
                driver.findElement(By.linkText(catalogEntry)).click();
                Thread.sleep(2000);
            }
            catch (Exception e){
                System.out.println("CANT CLICK ELEMENT");
            }
        }
    }
}
