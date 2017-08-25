package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{AuthService, DatabaseService}

import scala.concurrent.{ExecutionContext, Future}

class AuthController(cc: ControllerComponents, databaseService: DatabaseService, authService: AuthService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def loginPage = Action {
    Ok(views.html.login())
  }

  def login: Action[AnyContent] = Action.async { implicit request =>
    AuthController.loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest),
      userLoginData => {
        for {
          cookieOption <- authService.login(userLoginData.email, userLoginData.password)
        } yield {
          cookieOption match {
            case Some(cookie) => Redirect("/").withCookies(cookie)
            case None => Ok(views.html.login())
          }
        }
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
        authService.signup(userSignupData.email, userSignupData.password)
        Ok(views.html.login())
      }
    )
  }

}

object AuthController {

  case class UserLoginData(email: String, password: String)

  val loginForm = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserSignupData.apply)(UserSignupData.unapply)
  }

  case class UserSignupData(email: String, password: String)

  val signupForm = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserSignupData.apply)(UserSignupData.unapply)
  }

}
