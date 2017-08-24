package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.DatabaseService

import scala.concurrent.ExecutionContext

class AuthController(cc: ControllerComponents, databaseService: DatabaseService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def loginPage = Action {
    Ok(views.html.login())
  }

  def login = Action { implicit request =>
    AuthController.loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      userLoginData => {
        Ok(views.html.index())
      }
    )
  }

  def signupPage = Action {
    Ok(views.html.signup())
  }

  def signup = Action { implicit request =>
    AuthController.signupForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      userSignupData => {
        databaseService.addUser(userSignupData.username, userSignupData.password)
        Ok(views.html.index())
      }
    )
  }

}

object AuthController {

  case class UserLoginData(username: String, password: String)

  val loginForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserSignupData.apply)(UserSignupData.unapply)
  }

  case class UserSignupData(username: String, password: String)

  val signupForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserSignupData.apply)(UserSignupData.unapply)
  }

}
