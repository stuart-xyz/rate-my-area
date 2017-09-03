package controllers

import controllers.ReviewController.ReviewFormData
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{DatabaseService, UserAuthAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ReviewController(cc: ControllerComponents, databaseService: DatabaseService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def create = userAuthAction { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[ReviewFormData].fold(
        errors => BadRequest(Json.obj("error" -> "Invalid data supplied")),
        reviewFormData =>
          databaseService.addReview(request.user, reviewFormData) match {
            case Success(_) => Ok(Json.obj("message" -> "Review added successfully"))
            case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error"))
          }
      )
      case None => BadRequest(Json.obj("error" -> "Expected JSON body"))
    }
  }

  def list: Action[AnyContent] = userAuthAction.async { implicit request =>
    databaseService.listReviews match {
      case Success(result) => result.map(reviews => Ok(Json.toJson(reviews)))
      case Failure(_) => Future.successful(InternalServerError(Json.obj("error"-> "Unexpected internal error")))
    }
  }

}

object ReviewController {

  case class ReviewFormData(title: String, areaName: String, description: String, imageUrls: List[String])
  implicit val reviewFormDataFormat: OFormat[ReviewFormData] = Json.format[ReviewFormData]

}
