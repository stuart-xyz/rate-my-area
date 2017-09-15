package services

import controllers.ReviewController.ReviewFormData
import models._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class DatabaseService(dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {

  private val users = TableQuery[UserTable]
  private val reviews = TableQuery[ReviewTable]
  private val imageUrls = TableQuery[ImageUrlTable]

  def addUser(email: String, username: String, hashedPassword: String, salt: String): Future[Try[Int]] = {
    val query = (users returning users.map(_.id)) += User(0, email, hashedPassword, salt, username)
    dbConfig.db.run(query.asTry)
  }

  def getUserOption(email: String): Future[Try[Option[User]]] = {
    dbConfig.db.run(users.filter(_.email.toLowerCase === email.toLowerCase).result.headOption.asTry)
  }

  def listReviews: Future[Try[Seq[DisplayedReview]]] = {
    val joinQuery = for {
      ((review, user), imageUrl) <- reviews join users on (_.userId === _.id) joinLeft imageUrls on (_._1.id === _.reviewId)
    } yield (review, user, imageUrl)

    val mergeQuery = joinQuery.result.map(results => {
      val groupedByReviewId = results.groupBy {
        case (review, _, _) => review.id
      }
      groupedByReviewId.map {
        case (_, group) =>
          val imageUrls = group.flatMap {
            case (_, _, imageUrlOption) => imageUrlOption.map(_.url)
          }
          DisplayedReview(group.head._1, group.head._2.username, imageUrls)
      }.toSeq
    })
    dbConfig.db.run(mergeQuery.asTry)
  }

  def addReview(user: User, reviewFormData: ReviewFormData): Future[Try[Seq[Int]]] = {
    val addReviewQuery = (reviews returning reviews.map(_.id)) += Review(0, reviewFormData.title, reviewFormData.areaName, reviewFormData.description, user.id)
    val futureResult = for {
      reviewIdTry <- dbConfig.db.run(addReviewQuery.asTry)
    } yield reviewIdTry match {
      case Success(reviewId) =>
        val addImageUrlsQuery = (imageUrls returning imageUrls.map(_.id)) ++= reviewFormData.imageUrls.map(url => ImageUrl(0, url, reviewId))
        dbConfig.db.run(addImageUrlsQuery.asTry)
      case Failure(_) => Future.successful(Failure(new Exception("Database error")))
    }
    futureResult.flatMap(identity)
  }

  def updateReview(updatedReview: Review): Future[Try[Int]] = {
    val updateQuery = for {
      review <- reviews
    } yield (review.title, review.areaName, review.description)
    dbConfig.db.run(updateQuery.update(updatedReview.title, updatedReview.areaName, updatedReview.description).asTry)
  }

  def deleteReview(reviewId: Int): Future[Try[Int]] = {
    dbConfig.db.run(reviews.filter(_.id === reviewId).delete.asTry)
  }

}
