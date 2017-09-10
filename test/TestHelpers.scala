import org.scalatest.Assertion
import play.api.libs.json.JsObject
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TestHelpers extends AuthenticatedUser {

  def maybeAddAuthCookie[A](fakeRequest: FakeRequest[A], authenticated: Boolean, cookie: Cookie): FakeRequest[A] = {
    if (authenticated) fakeRequest.withCookies(cookie)
    else fakeRequest
  }

  def simpleRequestCheck(endpoint: String, authenticated: Boolean, jsonBody: Option[JsObject], method: String, expectedStatus: Int, jsonKeyToCheck: String): Future[Assertion] = {
    for {
      authCookie <- getAuthCookie
    } yield {
      val result = method match {
        case GET =>
          val request = maybeAddAuthCookie(FakeRequest(GET, s"/$endpoint"), authenticated, authCookie)
          route(app, request).get
        case POST =>
          val request = maybeAddAuthCookie(FakeRequest(POST, s"/$endpoint").withJsonBody(jsonBody.get), authenticated, authCookie)
          route(app, request).get
      }
      status(result) mustBe expectedStatus
      contentType(result) mustBe Some("application/json")
      (contentAsJson(result) \ jsonKeyToCheck).isDefined mustBe true
    }
  }

}
