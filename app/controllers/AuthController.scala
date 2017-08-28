package controllers

import models.Response
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{AuthService, DatabaseService, UserAuthAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AuthController(cc: ControllerComponents, databaseService: DatabaseService, authService: AuthService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def login: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[AuthController.UserLoginData].fold(
        errors => Future.successful(BadRequest("Expected username and password")),
        userLoginData => {
          val resultAttempt = for {
            cookieOptionFuture <- authService.login(userLoginData.email, userLoginData.password)
          } yield for {
            cookieOption <- cookieOptionFuture
          } yield cookieOption match {
            case Some(cookie) => Ok("Log in successful").withCookies(cookie)
            case None => Unauthorized("Invalid login credentials provided")
          }

          resultAttempt match {
            case Success(result) => result
            case Failure(_) => Future.successful(InternalServerError("Unexpected internal error occurred"))
          }
        }
      )
      case None => Future.successful(BadRequest("Expected JSON body"))
    }
  }

  def signup = Action { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[AuthController.UserSignupData].fold(
        errors => BadRequest(Response("Expected username and password", hasError = true).json),
        userSignupData =>
          authService.signup(userSignupData.email, userSignupData.password) match {
            case Success(_) => Ok("Sign up successful")
            case Failure(_) => InternalServerError(Response("Unexpected internal error occurred", hasError = true).json)
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
