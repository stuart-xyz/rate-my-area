import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import controllers.UploadController
import models.User
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AuthService, S3Service, UserAuthAction}

import scala.concurrent.ExecutionContext.Implicits.global

class UploadControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory with MockitoSugar {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val mockS3Service: S3Service = mock[S3Service]
  when(mockS3Service.upload(any[File], any[String], any[Int])) thenReturn "test"

  val mockUser = Some(User(1, "email@email.com", "hashed-password", "salt", "username"))
  val mockAuthService: AuthService = mock[AuthService]
  when(mockAuthService.checkCookie(any[RequestHeader])) thenReturn mockUser

  val mockUserAuthAction = new UserAuthAction(stubBodyParser(), mockAuthService)
  val mockUploadController = new UploadController(stubControllerComponents(playBodyParsers = stubPlayBodyParsers), mockUserAuthAction, mockS3Service)

  "POST /upload" should {

    "return HTTP 200 ok with an authorisation cookie" in {
      val temporaryFile = Files.SingletonTemporaryFileCreator.create(app.environment.getFile("test/resources/dalston-small-1.jpg").toPath)
      val part = FilePart[TemporaryFile](key = "photo", filename = "photo", contentType = None, ref = temporaryFile)
      val request = FakeRequest(POST, "/upload").withMultipartFormDataBody(
        MultipartFormData[TemporaryFile](dataParts = Map.empty, files = Seq(part), badParts = Nil)
      )

      val cookie = getAuthCookie
      val action = mockUploadController.upload(request.withCookies(cookie))
      status(action) mustBe OK
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val temporaryFile = Files.SingletonTemporaryFileCreator.create(app.environment.getFile("test/resources/dalston-small-1.jpg").toPath)
      val part = FilePart[TemporaryFile](key = "photo", filename = "photo", contentType = None, ref = temporaryFile)
      val request = FakeRequest(POST, "/upload").withMultipartFormDataBody(
        MultipartFormData[TemporaryFile](dataParts = Map.empty, files = Seq(part), badParts = Nil)
      )

      val futureResult = route(app, request).get
      validateResult(futureResult, UNAUTHORIZED, "error")
    }

  }

}
