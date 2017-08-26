package services

import models.{User, UserTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class DatabaseService(dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  private val users = TableQuery[UserTable]

  def listUsers: Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }

  def addUser(email: String, hashedPassword: String, salt: String): Future[Int] = {
    dbConfig.db.run {
      (users returning users.map(_.id)) += User(0, email, hashedPassword, salt, "name", isAdmin = true)
    }
  }

  def getUserOption(email: String): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.email === email).result.headOption)
  }

}
