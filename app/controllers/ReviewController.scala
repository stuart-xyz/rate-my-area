package controllers

import controllers.ReviewController.ReviewFormData
import models.Review
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{DatabaseService, S3Service, UserAuthAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ReviewController(cc: ControllerComponents, databaseService: DatabaseService, userAuthAction: UserAuthAction, s3Service: S3Service)(implicit ec: ExecutionContext)
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
    for {
      reviewsTry <- databaseService.listReviews
    } yield reviewsTry match {
      case Success(reviews) => Ok(Json.toJson(reviews))
      case Failure(_) => InternalServerError(Json.obj("error"-> "Unexpected internal error"))
    }
  }

  def delete(id: Int): Action[AnyContent] = userAuthAction.async { implicit request =>

    case class ReviewNotFoundException() extends Exception
    case class UnexpectedErrorException() extends Exception

    def deleteReviewFromDatabase(review: Review) = {
      if (request.user.id == review.userId) {
        for {
          resultTry <- databaseService.deleteReview(id)
        } yield resultTry match {
          case Success(_) => Ok(Json.obj("message" -> "Review deleted successfully"))
          case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error"))
        }
      } else Future.successful(Unauthorized(Json.obj("error" -> "Not authorised to delete this review")))
    }

    val futureReviewTry = for {
      reviewsTry <- databaseService.listReviews
    } yield reviewsTry match {
      case Success(reviews) =>
        val filteredReviews = reviews.filter(_.review.id == id)
        if (filteredReviews.isEmpty) Failure(ReviewNotFoundException())
        else {
          val review = filteredReviews.head
          review.imageUrls.foreach(imageUrl => s3Service.delete(imageUrl))
          Success(review)
        }
      case Failure(_) => Failure(UnexpectedErrorException())
    }

    val nestedFutureResult = for {
      reviewTry <- futureReviewTry
    } yield reviewTry match {
      case Success(displayedReview) => deleteReviewFromDatabase(displayedReview.review)
      case Failure(_: ReviewNotFoundException) => Future.successful(BadRequest(Json.obj("error" -> "Review does not exist")))
      case Failure(_: UnexpectedErrorException) => Future.successful(InternalServerError(Json.obj("error" -> "Unexpected internal error")))
    }
    nestedFutureResult.flatMap(identity)
  }

}

object ReviewController {

  case class ReviewFormData(title: String, areaName: String, description: String, imageUrls: List[String])
  implicit val reviewFormDataFormat: OFormat[ReviewFormData] = Json.format[ReviewFormData]

}
