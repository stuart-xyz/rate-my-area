package modules

import controllers.HomeController
import play.api.mvc.ControllerComponents
import services.DatabaseService

import scala.concurrent.ExecutionContext

trait ControllerModule {

  import com.softwaremill.macwire._

  // dependencies
  implicit def ec: ExecutionContext
  def databaseService: DatabaseService
  def controllerComponents: ControllerComponents

  lazy val homeController: HomeController = wire[HomeController]

}
