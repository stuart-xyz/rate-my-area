import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

class IndexControllerSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory {

  "GET /" should {

    "return the React view" in {
      val request = FakeRequest(GET, "/")
      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("RateMyArea")
    }

  }

}
