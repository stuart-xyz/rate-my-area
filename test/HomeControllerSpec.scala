import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory {

  "HomeController GET" should {

    "redirect unauthorised users to the login page" in {
      val request = FakeRequest(GET, "/")
      val result = route(app, request).get

      status(result) mustBe SEE_OTHER
//      contentType(result) mustBe Some("text/html")
//      contentAsString(result) must include ("Welcome to Play")
    }

  }

}
