package services

import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserAuthAction(val parser: BodyParser[AnyContent], authService: AuthService)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserAuthRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: (UserAuthRequest[A]) => Future[Result]): Future[Result] = {
    authService.checkCookie(request) match {
      case None => Future.successful(Results.Unauthorized)
      case Some(user) => block(UserAuthRequest(user, request))
    }
  }

}
