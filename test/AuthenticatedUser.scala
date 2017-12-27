import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AuthenticatedUser extends PlaySpec {

  implicit def app: Application

  val credentials: JsObject = Json.obj(
    "username" -> "username",
    "email" -> "email@email.com",
    "password" -> "password"
  )

  def signupWithValidCredentials(): Future[Result] = {
    val signupRequest = FakeRequest(POST, "/signup").withJsonBody(credentials)
    route(app, signupRequest).get
  }

  def loginWithValidCredentials(): Future[Result] = {
    val futureResult = for {
      _ <- signupWithValidCredentials()
    } yield {
      val loginRequest = FakeRequest(POST, "/login").withJsonBody(credentials)
      val loginResult = route(app, loginRequest).get
      loginResult
    }
    futureResult.flatMap(identity)
  }

}
