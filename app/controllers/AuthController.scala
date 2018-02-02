package controllers

import controllers.AuthController.{UserLoginData, UserSignupData}
import org.postgresql.util.PSQLException
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import services.CustomExceptions.UserNotLoggedInException
import services.{AuthService, DatabaseService}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AuthController(cc: ControllerComponents, databaseService: DatabaseService, authService: AuthService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  private val cookieHeader = "X-Auth-Token"

  def login: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[UserLoginData].fold(
        errors => Future.successful(BadRequest(Json.obj("error" -> "Expected username and password"))),
        userLoginData => {
          for {
            cookieOptionTry <- authService.login(userLoginData.email, userLoginData.password)
          } yield {
            cookieOptionTry match {
              case Success(cookieOption) => cookieOption match {
                case Some(cookie) => Ok(Json.obj("message" -> "Log in successful")).withCookies(cookie)
                case None => Unauthorized(Json.obj("error" -> "Invalid login credentials provided"))
              }
              case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error occurred"))
            }
          }
        }
      )
      case None => Future.successful(BadRequest(Json.obj("error" -> "Expected JSON body")))
    }
  }

  def signup: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[UserSignupData].fold(
        errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid email, username or password"))),
        userSignupData =>
          for {
            result <- authService.signup(userSignupData.email, userSignupData.username, userSignupData.password)
          } yield result match {
            case Success(_) => Ok(Json.obj("message" -> "Signup successful"))
            case Failure(e: PSQLException) => Conflict(Json.obj("error" -> e.getServerErrorMessage.getConstraint))
            case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error occurred"))
          }
      )
      case None => Future.successful(BadRequest(Json.obj("error" -> "Expected JSON body")))
    }
  }

  def logout = userAuthAction { implicit request =>
    authService.logout(request) match {
      case None => Unauthorized(Json.obj("error" -> "Unauthorised"))
      case Some(removeCookieAttempt) =>
        removeCookieAttempt match {
          case Success(_) => Ok(Json.obj("message" -> "Successfully logged out")).discardingCookies(DiscardingCookie(cookieHeader))
          case Failure(_: UserNotLoggedInException) => BadRequest(Json.obj("error" -> "The specified authentication token is not active"))
          case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error"))
        }
    }
  }

  def getUser = userAuthAction { implicit request =>
    Ok(Json.toJsObject(request.user) - "hashedPassword" - "salt")
  }

}

object AuthController {

  case class UserLoginData(email: String, password: String)
  case class UserSignupData(email: String, username: String, password: String)

  private def emailRegex =
    "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$".r

  implicit val userLoginDataReads: Reads[UserLoginData] = Json.reads[UserLoginData]
  implicit val userSignupDataReads: Reads[UserSignupData] = (
    (JsPath \ "email").read[String].filter(JsonValidationError("Invalid email address"))(emailRegex.findFirstIn(_).isDefined) and
    (JsPath \ "username").read[String].filter(JsonValidationError("Invalid username"))(_.length > 0) and
    (JsPath \ "password").read[String].filter(JsonValidationError("Invalid password"))(_.length > 0)
  )(UserSignupData.apply _)

}
