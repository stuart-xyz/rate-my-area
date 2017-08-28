package controllers

import models.{Response, Review}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{DatabaseService, UserAuthAction}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ReviewController(cc: ControllerComponents, databaseService: DatabaseService, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def create = userAuthAction { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[Review].fold(
        errors => BadRequest("Invalid data supplied"),
        review =>
          databaseService.addReview(request.user, review) match {
            case Success(_) => Ok(Response("Review added successfully", hasError = false).json)
            case Failure(_) => InternalServerError(Response("Review added successfully", hasError = true).json)
          }
      )
      case None => BadRequest(Response("Expected JSON body", hasError = true).json)
    }
  }

}
