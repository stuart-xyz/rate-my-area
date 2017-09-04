package services

import controllers.ReviewController.ReviewFormData
import models._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class DatabaseService(dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {

  private val users = TableQuery[UserTable]
  private val reviews = TableQuery[ReviewTable]
  private val imageUrls = TableQuery[ImageUrlTable]

  def listUsers: Try[Future[Seq[User]]] = Try {
    dbConfig.db.run(users.result)
  }

  def addUser(email: String, username: String, hashedPassword: String, salt: String): Try[Future[Int]] = Try {
    dbConfig.db.run {
      (users returning users.map(_.id)) += User(0, email, hashedPassword, salt, username)
    }
  }

  def getUserOption(email: String): Try[Future[Option[User]]] = Try {
    dbConfig.db.run(users.filter(_.email.toLowerCase === email.toLowerCase).result.headOption)
  }

  def listReviews: Try[Future[Seq[ReviewWithImageUrls]]] = Try {
    val joinLeftQuery = for {
      (review, imageUrl) <- reviews joinLeft imageUrls on (_.id === _.reviewId)
    } yield (review, imageUrl)

    val mergeQuery = joinLeftQuery.result.map(results => {
      val groupedByReviewId = results.groupBy {
        case (review, _) => review.id
      }
      groupedByReviewId.map {
        case (_, group) =>
          val imageUrls = group.flatMap(_._2.map(_.url))
          ReviewWithImageUrls(group.head._1, imageUrls)
      }.toSeq
    })
    dbConfig.db.run(mergeQuery)
  }

  def addReview(user: User, reviewFormData: ReviewFormData): Try[Future[Seq[Int]]] = Try {
    val reviewIdFuture = dbConfig.db.run {
      (reviews returning reviews.map(_.id)) += Review(0, reviewFormData.title, reviewFormData.areaName, reviewFormData.description, user.id)
    }
    val idsNestedFuture = for {
      reviewId <- reviewIdFuture
    } yield {
      dbConfig.db.run {
        (imageUrls returning imageUrls.map(_.id)) ++= reviewFormData.imageUrls.map(url => ImageUrl(0, url, reviewId))
      }
    }
    idsNestedFuture.flatMap(identity)
  }

}
