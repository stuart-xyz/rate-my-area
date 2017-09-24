import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.api.test.FakeRequest
import play.api.test.Helpers._

class UploadControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory {

  "POST /upload" should {

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val temporaryFile = Files.SingletonTemporaryFileCreator.create(app.environment.getFile("test/resources/dalston-small-1.jpg").toPath)
      val part = FilePart[TemporaryFile](key = "photo", filename = "photo", contentType = None, ref = temporaryFile)
      val request = FakeRequest().withMultipartFormDataBody(
        MultipartFormData[TemporaryFile](dataParts = Map.empty, files = Seq(part), badParts = Nil)
      )

      val futureResult = route(app, request).get
      validateResult(futureResult, UNAUTHORIZED, "error")
    }

  }

}
