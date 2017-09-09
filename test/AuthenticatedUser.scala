import org.scalatest.Assertion
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Cookie, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, route, status, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AuthenticatedUser extends PlaySpec {

  implicit def app: Application

  val credentials: JsObject = Json.obj(
    "username" -> "username",
    "email" -> "email@email.com",
    "password" -> "password"
  )

  def signupWithValidCredentials(): Assertion = {
    val signupRequest = FakeRequest(POST, "/signup").withJsonBody(credentials)
    val signupResult = route(app, signupRequest).get
    status(signupResult) mustBe OK
  }

  def loginWithValidCredentials(): Future[Result] = {
    signupWithValidCredentials()
    val loginRequest = FakeRequest(POST, "/login").withJsonBody(credentials)
    val loginResult = route(app, loginRequest).get
    status(loginResult) mustBe OK
    loginResult
  }

  def getAuthCookie: Future[Cookie] = {
    loginWithValidCredentials().map(loginResult => Cookie("X-Auth-Token", loginResult.header.headers("X-Auth-Token")))
  }

}
