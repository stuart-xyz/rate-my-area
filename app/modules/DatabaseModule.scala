package modules

import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.{DbName, SlickComponents}
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import services.DatabaseService
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait DatabaseModule extends SlickComponents
  with EvolutionsComponents
  with SlickEvolutionsComponents {

  import com.softwaremill.macwire._

  // dependencies
  implicit def ec: ExecutionContext

  lazy val dbConfig = slickApi.dbConfig[JdbcProfile](DbName("default"))
  lazy val databaseService: DatabaseService = wire[DatabaseService]

}
