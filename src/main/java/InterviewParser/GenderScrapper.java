package InterviewParser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GenderScrapper
{
    public static void main(String[] args)
    {
        GenderScrapper scrapper = new GenderScrapper("CHROME");
        System.out.println(scrapper.getGender("Alex"));
        System.out.println(scrapper.getGender("Katie"));
        scrapper.finish();
    }

    private WebDriver driver;
    private String baseUrl;
    private String catalogEntry = "";

    public GenderScrapper(String type)
    {
        if(type.equalsIgnoreCase("FIREFOX")) {
            System.setProperty("webdriver.gecko.driver", "H:\\workspace\\Holocaust\\libs\\geckodriver.exe");
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.dir", "H:\\workspace\\Holocaust\\WebDriverDownloads");
            profile.setPreference("browser.download.folderList", 2);

            //Set Preference to not show file download confirmation dialogue using MIME types Of different file extension types.
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    "application/pdf,application/x-pdf");

            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("pdfjs.disabled", true);

            driver = new FirefoxDriver(profile);
        }
        else if(type.equalsIgnoreCase("CHROME")) {
            System.setProperty("webdriver.chrome.driver", "H:\\workspace\\Holocaust\\libs\\chromedriver.exe");
            driver = new ChromeDriver();
        }
        else{
            return;
        }
        baseUrl = "http://genderchecker.com/default.aspx";
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public String getGender(String name)
    {
        driver.get(baseUrl);

//        try {
//            driver.manage().timeouts().wait(2000);
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }
        driver.findElement(By.id("ctl00_TextBoxName")).sendKeys(name);

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        driver.findElement(By.id("ctl00_ImageButtonSearch")).click();

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        String gender = "unknown";

        try {
            WebElement genderElement = driver.findElement(By.id("ctl00_ContentPlaceHolder1_LabelGenderFound"));

            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            if (null != genderElement) {
                gender = genderElement.getText();
            }
        }
        catch (Exception e){

        }
        System.out.println(name + " - " + gender);
        return gender;
    }
    public void finish(){

        driver.close();
    }
}
