import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._

class ReviewControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory {

  val sampleReview: JsObject = Json.obj(
    "title" -> "title",
    "areaName" -> "area",
    "description" -> "description",
    "imageUrls" -> Json.arr("https://image.user-content.ratemyarea.stuartp.io")
  )

  "GET /reviews" should {
    "return HTTP 200 ok with authorisation cookie" in simpleRequestCheck("reviews", authenticated = true, jsonBody = None, GET, OK, "title")
    "return HTTP 401 unauthorised without authorisation cookie" in simpleRequestCheck("reviews", authenticated = false, jsonBody = None, GET, UNAUTHORIZED, "error")
  }

  "POST /reviews" should {
    "return HTTP 200 ok with authorisation cookie" in simpleRequestCheck("reviews", authenticated = true, jsonBody = Some(sampleReview), POST, OK, "message")
    "return HTTP 401 unauthorised without an authorisation cookie" in simpleRequestCheck("reviews", authenticated = false, jsonBody = Some(sampleReview), POST, UNAUTHORIZED, "error")
  }

}
