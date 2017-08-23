package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import services.DatabaseService

import scala.concurrent.ExecutionContext

class HomeController(cc: ControllerComponents, databaseService: DatabaseService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    for {
      users <- databaseService.listUsers
    } yield Ok(views.html.index(Json.toJson(users).toString))
  }

  def signupPage = Action {
    Ok(views.html.signup())
  }

  def signup = Action { implicit request =>
    HomeController.signupForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      userSignupData => {
        databaseService.addUser(userSignupData.username, userSignupData.password)
        Ok(views.html.signup())
      }
    )
  }

}

object HomeController {

  case class UserSignupData(username: String, password: String)

  val signupForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserSignupData.apply)(UserSignupData.unapply)
  }

}
