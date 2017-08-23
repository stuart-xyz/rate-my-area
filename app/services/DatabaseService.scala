package services

import models.{User, UserTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class DatabaseService(dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  def getAllUsers: Future[Seq[User]] = {
    val query = TableQuery[UserTable]
    dbConfig.db.run(query.result)
  }

}
