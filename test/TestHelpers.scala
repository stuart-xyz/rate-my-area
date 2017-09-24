import org.scalatest.Assertion
import play.api.libs.json.JsObject
import play.api.mvc.{Cookie, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TestHelpers extends AuthenticatedUser {

  def maybeAddAuthCookie[A](fakeRequest: FakeRequest[A], authCookieOption: Option[Cookie]): FakeRequest[A] = {
    if (authCookieOption.isDefined) fakeRequest.withCookies(authCookieOption.get)
    else fakeRequest
  }

  def makeSimpleRequest(endpoint: String, authCookieOption: Option[Cookie], jsonBody: Option[JsObject], method: String): Future[Result] = {
    method match {
      case GET =>
        val request = maybeAddAuthCookie(FakeRequest(GET, s"/$endpoint"), authCookieOption)
        route(app, request).get
      case POST =>
        val request = maybeAddAuthCookie(FakeRequest(POST, s"/$endpoint").withJsonBody(jsonBody.get), authCookieOption)
        route(app, request).get
      case PATCH =>
        val request = maybeAddAuthCookie(FakeRequest(PATCH, s"/$endpoint").withJsonBody(jsonBody.get), authCookieOption)
        route(app, request).get
      case DELETE =>
        val request = maybeAddAuthCookie(FakeRequest(DELETE, s"/$endpoint"), authCookieOption)
        route(app, request).get
    }
  }

  def getAuthCookie: Cookie = {
    val loginResult = for {
      _ <- signupWithValidCredentials()
      result <- loginWithValidCredentials()
    } yield result
    cookies(loginResult).get("X-Auth-Token").getOrElse(throw new RuntimeException("Expected auth cookie"))
  }

  def validateResult(futureResult: Future[Result], expectedStatus: Int, jsonKeyToCheck: String): Assertion = {
    status(futureResult) mustBe expectedStatus
    contentType(futureResult) mustBe Some("application/json")
    (contentAsJson(futureResult) \\ jsonKeyToCheck).nonEmpty mustBe true
  }

}
