import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Cookie
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global

class AuthControllerSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory {

  "GET /user" should {

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val request = FakeRequest(GET, "/user")
      val result = route(app, request).get
      status(result) mustBe UNAUTHORIZED
      contentType(result) mustBe Some("application/json")
      (contentAsJson(result) \ "error").isDefined mustBe true
    }

    "return HTTP 200 ok with authorisation cookie" in {
      val credentials: JsObject = Json.obj(
        "username" -> "username",
        "email" -> "email@email.com",
        "password" -> "password"
      )

      val signupRequest = FakeRequest(POST, "/signup").withJsonBody(credentials)
      val signupResult = route(app, signupRequest).get
      status(signupResult) mustBe OK

      val loginRequest = FakeRequest(POST, "/login").withJsonBody(credentials)
      val loginResult = route(app, loginRequest).get
      status(loginResult) mustBe OK

      loginResult.map(loginResult => {
        val cookie = Cookie("X-Auth-Token", loginResult.header.headers("X-Auth-Token"))
        val request = FakeRequest(GET, "/user").withCookies(cookie)
        val result = route(app, request).get
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
        (contentAsJson(result) \ "message").isDefined mustBe true
      })
    }

  }

}
