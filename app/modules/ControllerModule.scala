package modules

import controllers._
import play.api.ApplicationLoader.Context
import play.api.Configuration
import play.api.cache.SyncCacheApi
import play.api.mvc.{AnyContent, BodyParser, ControllerComponents}
import services.{AuthService, DatabaseService, S3Service}

import scala.concurrent.ExecutionContext

trait ControllerModule {

  import com.softwaremill.macwire._

  // dependencies
  implicit def ec: ExecutionContext
  def databaseService: DatabaseService
  def controllerComponents: ControllerComponents
  def bodyParser: BodyParser[AnyContent]
  def defaultSyncCacheApi: SyncCacheApi
  def appConfig: Configuration
  def context: Context
  def s3ServiceOverride: Option[S3Service]

  lazy val authService = new AuthService(defaultSyncCacheApi, databaseService)
  lazy val s3Service: S3Service =
    if (s3ServiceOverride.isDefined) s3ServiceOverride.get
    else new S3Service(appConfig)
  lazy val userAuthAction: UserAuthAction = wire[UserAuthAction]

  lazy val indexController: IndexController = wire[IndexController]
  lazy val authController: AuthController = wire[AuthController]
  lazy val reviewController: ReviewController = wire[ReviewController]
  lazy val uploadController: UploadController = wire[UploadController]

}
