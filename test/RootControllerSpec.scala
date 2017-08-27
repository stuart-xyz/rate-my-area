import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

class RootControllerSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory {

  "RootController GET" should {

    "redirect unauthorised users to the login page" in {
      val request = FakeRequest(GET, "/")
      val result = route(app, request).get
      status(result) mustBe SEE_OTHER
    }

  }

}
