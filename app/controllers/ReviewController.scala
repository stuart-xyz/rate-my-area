package controllers

import controllers.ReviewController.ReviewFormData
import models.Response
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{DatabaseService, UserAuthAction}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ReviewController(cc: ControllerComponents, databaseService: DatabaseService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def create = userAuthAction { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[ReviewFormData].fold(
        errors => BadRequest("Invalid data supplied"),
        reviewFormData =>
          databaseService.addReview(request.user, reviewFormData) match {
            case Success(_) => Ok(Response("Review added successfully", hasError = false).json)
            case Failure(_) => InternalServerError(Response("Review added successfully", hasError = true).json)
          }
      )
      case None => BadRequest(Response("Expected JSON body", hasError = true).json)
    }
  }

}

object ReviewController {

  case class ReviewFormData(title: String, areaName: String, emojiCode: String, description: String)
  implicit val reviewFormDataFormat: OFormat[ReviewFormData] = Json.format[ReviewFormData]

}
