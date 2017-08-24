package controllers

import play.api.mvc._
import services.DatabaseService

import scala.concurrent.ExecutionContext

class HomeController(cc: ControllerComponents, databaseService: DatabaseService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    for {
      users <- databaseService.listUsers
    } yield Ok(views.html.index())
  }

}
