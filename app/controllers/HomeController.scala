package controllers

import play.api.mvc._
import services.{AuthService, DatabaseService, UserAuthAction, UserAuthRequest}

import scala.concurrent.ExecutionContext

class HomeController(cc: ControllerComponents, databaseService: DatabaseService, authService: AuthService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def index(): Action[AnyContent] = userAuthAction { implicit request: UserAuthRequest[AnyContent] =>
    Ok(views.html.index(request.user.fullName))
  }

}
