import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.By
import java.time.Duration


class QuoraScraper {

    fun createDriver(): WebDriver {
        println("Enter a Quora Username : ")
        val username = readLine()
        val driver = ChromeDriver()
        driver.manage().window().maximize()
        driver.get("https://www.quora.com/profile/" + username + "/answers")
        val amtsecs = Duration.ofSeconds(10)
        WebDriverWait(driver, amtsecs).until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class=\"q-box qu-borderBottom\"]"))
        )
        return driver
    }


}


fun main(args: Array<String>) {

    println("Hello World!")
    val scraper = QuoraScraper()
    val driver = scraper.createDriver()


}