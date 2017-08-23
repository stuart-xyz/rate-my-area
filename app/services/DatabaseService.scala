package services

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class DatabaseService(dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  def getUser: String = {
    "test"
  }

}
