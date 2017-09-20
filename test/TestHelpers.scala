import org.scalatest.Assertion
import play.api.libs.json.JsObject
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

trait TestHelpers extends AuthenticatedUser {

  def maybeAddAuthCookie[A](fakeRequest: FakeRequest[A], authenticated: Boolean): FakeRequest[A] = {
    if (authenticated) fakeRequest.withCookies(authCookie)
    else fakeRequest
  }

  def makeSimpleRequest(endpoint: String, authenticated: Boolean, jsonBody: Option[JsObject], method: String): Future[Result] = {
    method match {
      case GET =>
        val request = maybeAddAuthCookie(FakeRequest(GET, s"/$endpoint"), authenticated)
        route(app, request).get
      case POST =>
        val request = maybeAddAuthCookie(FakeRequest(POST, s"/$endpoint").withJsonBody(jsonBody.get), authenticated)
        route(app, request).get
    }
  }

  def validateResult(result: Future[Result], expectedStatus: Int, jsonKeyToCheck: String): Assertion = {
    println("---INFO--- " + contentAsJson(result).toString)
    status(result) mustBe expectedStatus
    contentType(result) mustBe Some("application/json")
    (contentAsJson(result) \\ jsonKeyToCheck).nonEmpty mustBe true
  }

}
