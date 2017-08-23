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

  def addUser(username: String, password: String): Future[User] = {
    dbConfig.db.run {
      (users returning users.map(_.id) into ((user, id) => user.copy(id = id))) +=
        User(0, username, password, "name", isAdmin = true)
    }
  }

}
