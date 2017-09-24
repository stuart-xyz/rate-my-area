package controllers

import controllers.ReviewController.{ReviewEditData, ReviewFormData}
import models.{DisplayedReview, Review}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, JsonValidationError, Reads}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{DatabaseService, S3Service, UserAuthAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ReviewController(cc: ControllerComponents, databaseService: DatabaseService, userAuthAction: UserAuthAction, s3Service: S3Service)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  class ReviewNotFoundException extends Exception

  def create: Action[AnyContent] = userAuthAction.async { implicit request =>
    request.body.asJson match {
      case Some(json) => json.validate[ReviewFormData].fold(
        errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid data supplied"))),
        reviewFormData =>
          for {
            resultTry <- databaseService.addReview(request.user, reviewFormData)
          } yield resultTry match {
            case Success(_) => Ok(Json.obj("message" -> "Review added successfully"))
            case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error"))
          }
      )
      case None => Future.successful(BadRequest(Json.obj("error" -> "Expected JSON body")))
    }
  }

  def edit(id: Int): Action[AnyContent] = userAuthAction.async { implicit request =>

    def mergeEdits(review: Review, reviewEditData: ReviewEditData) = review.copy(
      title = reviewEditData.title.getOrElse(review.title),
      areaName = reviewEditData.areaName.getOrElse(review.areaName),
      description = reviewEditData.description.getOrElse(review.description)
    )

    def updateReviewInDatabase(editedReview: Review) = {
      if (request.user.id == editedReview.userId) {
        for {
          resultTry <- databaseService.updateReview(editedReview)
        } yield resultTry match {
          case Success(_) => Ok(Json.obj("message" -> "Review updated successfully"))
          case Failure(_) => InternalServerError(Json.obj("error" -> "Unexpected internal error"))
        }
      } else Future.successful(Unauthorized(Json.obj("error" -> "Not authorised to edit this review")))
    }

    request.body.asJson match {
      case Some(json) => json.validate[ReviewEditData].fold(
        errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid data supplied"))),
        reviewEditData => {
          val futureResult = for {
            reviewTry <- getReview(id)
          } yield reviewTry match {
            case Success(displayedReview) =>
              val editedReview = mergeEdits(displayedReview.review, reviewEditData)
              updateReviewInDatabase(editedReview)
            case Failure(_: ReviewNotFoundException) => Future.successful(NotFound(Json.obj("error" -> "Review does not exist")))
            case Failure(_) => Future.successful(InternalServerError(Json.obj("error" -> "Unexpected internal error")))
          }
          futureResult.flatMap(identity)
        }
      )
      case None => Future.successful(BadRequest(Json.obj("error" -> "Expected JSON body")))
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

    val futureResult = for {
      reviewTry <- getReview(id)
    } yield reviewTry match {
      case Success(displayedReview) =>
        displayedReview.imageUrls.foreach(imageUrl => s3Service.delete(imageUrl))
        deleteReviewFromDatabase(displayedReview.review)
      case Failure(_: ReviewNotFoundException) => Future.successful(NotFound(Json.obj("error" -> "Review does not exist")))
      case Failure(_) => Future.successful(InternalServerError(Json.obj("error" -> "Unexpected internal error")))
    }
    futureResult.flatMap(identity)
  }

  private def getReview(id: Int): Future[Try[DisplayedReview]] = {
    for {
      reviewsTry <- databaseService.listReviews
    } yield reviewsTry match {
      case Success(reviews) =>
        val filteredReviews = reviews.filter(_.review.id == id)
        if (filteredReviews.isEmpty) Failure(new ReviewNotFoundException)
        else {
          val review = filteredReviews.head
          Success(review)
        }
      case Failure(_) => Failure(new Exception)
    }
  }

}

object ReviewController {

  case class ReviewFormData(title: String, areaName: String, description: String, imageUrls: List[String])
  case class ReviewEditData(title: Option[String], areaName: Option[String], description: Option[String])

  implicit val reviewFormDataReads: Reads[ReviewFormData] = (
    (JsPath \ "title").read[String].filter(JsonValidationError("Invalid title"))(_.length > 0) and
    (JsPath \ "areaName").read[String].filter(JsonValidationError("Invalid area name"))(_.length > 0) and
    (JsPath \ "description").read[String].filter(JsonValidationError("Invalid description"))(_.length > 0) and
    (JsPath \ "imageUrls").read[List[String]]
  )(ReviewFormData.apply _)

  implicit val reviewEditDataReads: Reads[ReviewEditData] = (
    (JsPath \ "title").readNullable[String].filter(JsonValidationError("Invalid title"))(_.forall(_.length > 0)) and
    (JsPath \ "areaName").readNullable[String].filter(JsonValidationError("Invalid area name"))(_.forall(_.length > 0)) and
    (JsPath \ "description").readNullable[String].filter(JsonValidationError("Invalid description"))(_.forall(_.length > 0))
  )(ReviewEditData.apply _)

}
