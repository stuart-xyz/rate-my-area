import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global

class AuthControllerSpec extends PlaySpec with AuthenticatedUser with BaseOneAppPerTest with AppApplicationFactory {

  "POST /signup" should {

    "return HTTP 200 ok with valid provided credentials" in signupWithValidCredentials()

    "return HTTP 400 bad request with invalid provided email address" in {
      val badCredentials = credentials ++ Json.obj("email" -> "abc")
      val signupRequest = FakeRequest(POST, "/signup").withJsonBody(badCredentials)
      val signupResult = route(app, signupRequest).get
      status(signupResult) mustBe BAD_REQUEST
    }

  }

  "POST /login" should {

    "return HTTP 200 ok with valid provided credentials" in loginWithValidCredentials()

    "return HTTP 401 unauthorised with invalid provided credentials" in {
      val badCredentials = credentials ++ Json.obj("email" -> "abc@123.com", "password" -> "xyz")
      val loginRequest = FakeRequest(POST, "/login").withJsonBody(badCredentials)
      val loginResult = route(app, loginRequest).get
      status(loginResult) mustBe UNAUTHORIZED
    }

  }

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
