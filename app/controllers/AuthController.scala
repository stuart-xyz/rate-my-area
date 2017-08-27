package controllers

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{AuthService, DatabaseService, UserAuthAction}

import scala.concurrent.{ExecutionContext, Future}

class AuthController(cc: ControllerComponents, databaseService: DatabaseService, authService: AuthService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def login: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[AuthController.UserLoginData].fold(
        errors => Future.successful(BadRequest("Expected username and password")),
        userLoginData =>
          for {
            cookieOption <- authService.login(userLoginData.email, userLoginData.password)
          } yield cookieOption match {
            case Some(cookie) => Ok("Log in successful").withCookies(cookie)
            case None => Unauthorized("Invalid login credentials provided")
          }
      )
      case None => Future.successful(BadRequest("Expected JSON body"))
    }
  }

  def signup = Action { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[AuthController.UserSignupData].fold(
        errors => BadRequest("Expected username and password"),
        userSignupData => {
          authService.signup(userSignupData.email, userSignupData.password)
          Ok("Sign up successful")
        }
      )
      case None => BadRequest("Expected JSON body")
    }
  }

  def getUser = userAuthAction { implicit request =>
    Ok(Json.stringify(Json.toJson(request.user)))
  }

}

object AuthController {

  case class UserLoginData(email: String, password: String)
  case class UserSignupData(email: String, password: String)

  implicit val userLoginDataFormat: OFormat[UserLoginData] = Json.format[UserLoginData]
  implicit val userSignupDataFormat: OFormat[UserSignupData] = Json.format[UserSignupData]

}
