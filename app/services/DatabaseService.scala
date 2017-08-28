package services

import models.{Review, ReviewTable, User, UserTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class DatabaseService(dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  private val users = TableQuery[UserTable]
  private val reviews = TableQuery[ReviewTable]

  def listUsers: Try[Future[Seq[User]]] = Try {
    dbConfig.db.run(users.result)
  }

  def addUser(email: String, hashedPassword: String, salt: String): Try[Future[Int]] = Try {
    dbConfig.db.run {
      (users returning users.map(_.id)) += User(0, email, hashedPassword, salt, "name", isAdmin = true)
    }
  }

  def getUserOption(email: String): Try[Future[Option[User]]] = Try {
    dbConfig.db.run(users.filter(_.email.toLowerCase === email.toLowerCase).result.headOption)
  }

  def addReview(user: User, review: Review): Try[Future[Int]] = Try {
    dbConfig.db.run {
      (reviews returning reviews.map(_.id)) += review.copy(userId = user.id)
    }
  }

}
