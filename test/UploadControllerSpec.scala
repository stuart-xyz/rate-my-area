import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.test.Helpers._

class UploadControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory {

  "POST /upload" should {

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("upload", authCookieOption = None, jsonBody = Some(Json.obj()), POST)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

}
