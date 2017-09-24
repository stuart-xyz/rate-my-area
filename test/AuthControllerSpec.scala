import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class AuthControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory {

  val badSignupCredentialsList = List(
    credentials ++ Json.obj("email" -> "abc"),
    credentials ++ Json.obj("username" -> ""),
    credentials ++ Json.obj("password" -> ""),
    credentials - "username",
    credentials - "email",
    credentials - "password"
  )

  val badLoginCredentialsList = List(
    credentials ++ Json.obj("email" -> "abc@123.com"),
    credentials ++ Json.obj("password" -> "xyz"),
    credentials ++ Json.obj("email" -> "abc@123.com", "password" -> "xyz")
  )

  val invalidStructuredLoginCredentialsList = List(
    credentials - "email",
    credentials - "password"
  )

  "POST /signup" should {

    "return HTTP 200 ok with valid signup credentials" in signupWithValidCredentials()

    "return HTTP 400 bad request with invalid signup credentials" in {
      val futureResults = for {
        badCredentials <- badSignupCredentialsList
      } yield {
        val signupRequest = FakeRequest(POST, "/signup").withJsonBody(badCredentials)
        route(app, signupRequest).get
      }

      futureResults.foreach(futureResult => status(futureResult) mustBe BAD_REQUEST)
    }

  }

  "POST /login" should {

    "return HTTP 200 ok with valid login credentials" in loginWithValidCredentials()

    "return HTTP 400 bad request with invalid structured login credentials" in {
      val futureResults = for {
        invalidCredentials <- invalidStructuredLoginCredentialsList
      } yield {
        val loginRequest = FakeRequest(POST, "/login").withJsonBody(invalidCredentials)
        route(app, loginRequest).get
      }

      futureResults.foreach(futureResult => status(futureResult) mustBe BAD_REQUEST)
    }

    "return HTTP 401 unauthorised with invalid login credentials" in {
      val futureResults = for {
        badCredentials <- badLoginCredentialsList
      } yield {
        val loginRequest = FakeRequest(POST, "/login").withJsonBody(badCredentials)
        route(app, loginRequest).get
      }

      futureResults.foreach(futureResult => status(futureResult) mustBe UNAUTHORIZED)
    }

  }

  "GET /user" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val result = makeSimpleRequest("user", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      validateResult(result, OK, "username")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("user", authCookieOption = None, jsonBody = None, GET)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

}
