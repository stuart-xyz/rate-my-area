package modules

import controllers.{AuthController, HomeController}
import play.api.cache.SyncCacheApi
import play.api.mvc.{AnyContent, BodyParser, ControllerComponents}
import services.{AuthService, DatabaseService, UserAuthAction}

import scala.concurrent.ExecutionContext

trait ControllerModule {

  import com.softwaremill.macwire._

  // dependencies
  implicit def ec: ExecutionContext
  def databaseService: DatabaseService
  def controllerComponents: ControllerComponents
  def bodyParser: BodyParser[AnyContent]
  def defaultSyncCacheApi: SyncCacheApi

  lazy val authService = new AuthService(defaultSyncCacheApi, databaseService)

  lazy val homeController: HomeController = wire[HomeController]
  lazy val authController: AuthController = wire[AuthController]
  lazy val userAuthAction: UserAuthAction = wire[UserAuthAction]

}
