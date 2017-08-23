package controllers

import play.api.libs.json.Json
import play.api.mvc._
import services.DatabaseService

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class HomeController(cc: ControllerComponents, databaseService: DatabaseService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    for {
      users <- databaseService.getAllUsers
    } yield Ok(views.html.index(Json.toJson(users).toString))
  }

}
