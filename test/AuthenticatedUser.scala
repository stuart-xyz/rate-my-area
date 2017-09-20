import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Cookie, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

trait AuthenticatedUser extends PlaySpec {

  implicit def app: Application

  val credentials: JsObject = Json.obj(
    "username" -> "username",
    "email" -> "email@email.com",
    "password" -> "password"
  )

  def signupWithValidCredentials(): Unit = {
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

  lazy val authCookie: Cookie = cookies(loginWithValidCredentials()).get("X-Auth-Token").getOrElse(throw new RuntimeException("Expected auth cookie"))

}
