import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global

class AuthControllerSpec extends PlaySpec with AuthenticatedUser with BaseOneAppPerTest with AppApplicationFactory {

  "GET /user" should {

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val request = FakeRequest(GET, "/user")
      val result = route(app, request).get
      status(result) mustBe UNAUTHORIZED
      contentType(result) mustBe Some("application/json")
      (contentAsJson(result) \ "error").isDefined mustBe true
    }

    "return HTTP 200 ok with authorisation cookie" in {
      for {
        authCookie <- getAuthCookie
      } yield {
        val request = FakeRequest(GET, "/user").withCookies(authCookie)
        val result = route(app, request).get
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
        (contentAsJson(result) \ "message").isDefined mustBe true
      }
    }

  }

}
