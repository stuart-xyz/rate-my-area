package controllers

import play.api.libs.json.Json
import play.api.mvc._
import services.AuthService

import scala.concurrent.{ExecutionContext, Future}

class UserAuthAction(val parser: BodyParser[AnyContent], authService: AuthService)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserAuthRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: (UserAuthRequest[A]) => Future[Result]): Future[Result] = {
    authService.checkJWT(request) match {
      case None => Future.successful(Results.Unauthorized(Json.obj("error" -> "Unauthorised")))
      case Some(user) => block(controllers.UserAuthRequest(user, request))
    }
  }

}
