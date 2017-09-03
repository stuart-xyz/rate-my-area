package modules

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import controllers.{AuthController, IndexController, ReviewController, UploadController}
import play.api.ApplicationLoader.Context
import play.api.Configuration
import play.api.cache.SyncCacheApi
import play.api.mvc.{AnyContent, BodyParser, ControllerComponents}
import services.{AuthService, DatabaseService, UploadService, UserAuthAction}

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

  private val s3Client = new S3ClientWrapper(appConfig, context.environment.mode)

  lazy val authService = new AuthService(defaultSyncCacheApi, databaseService)
  lazy val uploadService = new UploadService(appConfig, s3Client)
  lazy val userAuthAction: UserAuthAction = wire[UserAuthAction]

  lazy val indexController: IndexController = wire[IndexController]
  lazy val authController: AuthController = wire[AuthController]
  lazy val reviewController: ReviewController = wire[ReviewController]
  lazy val uploadController: UploadController = wire[UploadController]

}
