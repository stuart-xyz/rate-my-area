import java.io.File

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.Application
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.S3Service

class UploadControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory with MockitoSugar {

  val mockS3Service: S3Service = mock[S3Service]
  when(mockS3Service.upload(any[File], any[String], any[Int])) thenReturn "test"
  override def fakeApplication(): Application = new AppApplicationBuilder().build(Some(mockS3Service))

  "POST /upload" should {

    "return HTTP 200 ok with an authorisation cookie" in {
      val temporaryFiles = Seq(
        Files.SingletonTemporaryFileCreator.create(app.environment.getFile("test/resources/dalston-small-1.jpg").toPath),
        Files.SingletonTemporaryFileCreator.create(app.environment.getFile("test/resources/dalston-small-2.jpg").toPath)
      )
      val parts = temporaryFiles.map(temporaryFile => FilePart[TemporaryFile](key = "", filename = "", contentType = None, ref = temporaryFile))
      val request = FakeRequest(POST, "/upload").withMultipartFormDataBody(
        MultipartFormData[TemporaryFile](dataParts = Map.empty, files = parts, badParts = Nil)
      )

      val futureResult = route(app, request.withCookies(getAuthCookie)).get
      validateResult(futureResult, OK, "urls")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val temporaryFile = Files.SingletonTemporaryFileCreator.create(app.environment.getFile("test/resources/dalston-small-1.jpg").toPath)
      val part = FilePart[TemporaryFile](key = "", filename = "", contentType = None, ref = temporaryFile)
      val request = FakeRequest(POST, "/upload").withMultipartFormDataBody(
        MultipartFormData[TemporaryFile](dataParts = Map.empty, files = Seq(part), badParts = Nil)
      )

      val futureResult = route(app, request).get
      validateResult(futureResult, UNAUTHORIZED, "error")
    }

  }

}
