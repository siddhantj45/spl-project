import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.*
import java.time.Duration
import java.util.stream.Collectors
import java.util.stream.Stream


class QuoraScraper {

    fun escapeSpecialCharacters(data: String?): String? {
        var data = data ?: throw IllegalArgumentException("Input data cannot be null")
        var escapedData = data.replace("\\R".toRegex(), " ")
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"")
            escapedData = "\"" + data + "\""
        }
        return escapedData
    }

    fun convertToCSV(data: Array<String>): String? {
        return Stream.of<String>(*data)
            .map<String>(this::escapeSpecialCharacters)
            .collect(Collectors.joining(","))
    }

    fun createFinalCSV(dataLines: List<Array<String>>) {
        val csvOutputFile = File("questions_answers.csv")
        PrintWriter(csvOutputFile).use { pw ->
            dataLines.stream()
                .map { convertToCSV(it) } // Ensure convertToCSV has the correct signature
                .forEach { pw.println(it) }
        }
    }

    fun createDriver(): WebDriver {
        println("Enter a Quora Username : ")
        val username = readLine()
        //val username = "Marlene-Deloyer"
        //val username = "Ram-Teja-21"
        //val username = "Mark-Zuckerberg"
        val driver = ChromeDriver()
        driver.manage().window().maximize()
        driver.get("https://www.quora.com/profile/" + username + "/answers")
        val amtsecs = Duration.ofSeconds(10)
        WebDriverWait(driver, amtsecs).until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), \"Edits\")]"))
        )

        //Check if the user has posted any answers
        try {
            WebDriverWait(driver, amtsecs).until(
                ExpectedConditions.presenceOfElementLocated(By.ByXPath("//div[contains(@class, 'puppeteer_test_question_title')]"))
            )
        } catch (e: org.openqa.selenium.TimeoutException) {
            println("The user hasn't posted any answers.")
            return driver
        }

        // Delete the file if it already exists
        if (File("questions_answers.csv").exists()) {
            File("questions_answers.csv").delete()
        }

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
        val readMoreLinks =
            driver.findElements(By.xpath("//div[contains(@class, 'QTextTruncated__StyledReadMoreLink-sc-1pev100-3')][text()='(more)']"))

        // Click on each read more link
        for (readMoreLink in readMoreLinks) {
            val actions = Actions(driver)
            actions.moveToElement(readMoreLink).click().perform()
        }

        // Scroll to the top of the page
        (driver as JavascriptExecutor).executeScript("window.scrollTo(0, 0);")


        val questions: List<WebElement> =
            driver.findElements(By.ByXPath("//div[contains(@class, 'puppeteer_test_question_title')]"))
        val answers: List<WebElement> =
            driver.findElements(By.ByXPath("//div[contains(@class, 'spacing_log_answer_content puppeteer_test_answer_content')]"))

        val questionAndAnswerPairs = questions.zip(answers)

        val dataLines: MutableList<Array<String>> = ArrayList()
        dataLines.add(arrayOf("Questions", "Answers"))


        for ((question, answer) in questionAndAnswerPairs) {
            // Extract and concatenate text from span elements within the question div
            val questionText = StringBuilder()
            val questionSpans = question.findElements(By.tagName("span"))
            questionText.append(questionSpans[0].text)

            // Extract and concatenate text from span elements within the answer div
            val answerText = StringBuilder()
            val answerSpans = answer.findElements(By.tagName("span"))
            answerText.append(answerSpans[0].text)

            // Print the question and answer
            println("Question: $questionText")
            println("Answer: $answerText")
            println("\n\n\n")

            dataLines.add(arrayOf(questionText.toString(), answerText.toString()))


        }

        createFinalCSV(dataLines)


        return driver
    }


}


fun main(args: Array<String>) {
    val scraper = QuoraScraper()
    val driver = scraper.createDriver()
    driver.close()


}