import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration


class QuoraScraper {

    fun createDriver(): WebDriver {
        println("Enter a Quora Username : ")
        //val username = readLine()
//        val username = "Marlene-Deloyer"
        val username = "Ram-Teja-21"
        val driver = ChromeDriver()
        driver.manage().window().maximize()
        driver.get("https://www.quora.com/profile/" + username + "/answers")
        val amtsecs = Duration.ofSeconds(10)
        WebDriverWait(driver, amtsecs).until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), \"Edits\")]"))
        )

        // Get the current scroll height
        // Get the current scroll height
        var scrollHeight = (driver as JavascriptExecutor).executeScript("return document.body.scrollHeight") as Long


        // Keep scrolling until the scroll height remains the same
        while (true) {
            driver.executeScript("window.scrollTo(0, document.body.scrollHeight)")
            Thread.sleep(2000) // Wait for the page to load

            // Get the new scroll height
            val newScrollHeight =
                (driver as JavascriptExecutor).executeScript("return document.body.scrollHeight") as Long

            // Check if the scroll height has changed
            if (scrollHeight == newScrollHeight) {
                break
            }
            scrollHeight = newScrollHeight
        }


        // Scroll to the top of the page
        (driver as JavascriptExecutor).executeScript("window.scrollTo(0, 0);")

        // Find all Read More links
        val readMoreLinks = driver.findElements(By.xpath("//div[contains(@class, 'QTextTruncated__StyledReadMoreLink-sc-1pev100-3')][text()='(more)']"))

        // Click on each read more link
        for (readMoreLink in readMoreLinks) {
            val actions = Actions(driver)
            actions.moveToElement(readMoreLink).click().perform()
        }

        // Scroll to the top of the page
        (driver as JavascriptExecutor).executeScript("window.scrollTo(0, 0);")

        // Loop through each div element

        val divs: List<WebElement> = driver.findElements(By.ByXPath("//div[contains(@class, 'spacing_log_answer_content puppeteer_test_answer_content')]"))


        // Loop through each div element
        for (div in divs) {
            // Extract and concatenate text from span elements within the div
            val text = StringBuilder()
            val spans = div.findElements(By.tagName("span"))
            for (span in spans) {
                text.append(span.text)
            }
            // Read more button class = .QTextTruncated__StyledReadMoreLink-sc-1pev100-3
            // Question class = .puppeteer_test_question_title
            // Print the concatenated text
            println(text.toString())
            println("\n\n\n")
        }


//        val elements = driver.findElements(By.xpath("//span[@class='q-box qu-userSelect--text']/span"))
//
//        for (element in elements) {
//            val text = element.text
//            println(text) // Replace with your desired processing or printing logic
//        }

       // val element = driver.findElement(By.xpath("//span[@class='q-box qu-userSelect--text']/span"))
       // val txt = element.getText()
       // println(txt)
        return driver
    }


}


fun main(args: Array<String>) {

    println("Hello World!")
    val scraper = QuoraScraper()
    val driver = scraper.createDriver()
    //driver.close()


}