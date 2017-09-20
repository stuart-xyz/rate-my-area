import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global

class ReviewControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory {

  val sampleReview: JsObject = Json.obj(
    "title" -> "title",
    "areaName" -> "area",
    "description" -> "description",
    "imageUrls" -> Json.arr("https://image.user-content.ratemyarea.stuartp.io")
  )

  "POST /reviews" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authenticated = true, jsonBody = Some(sampleReview), POST)
      validateResult(result, OK, "message")
    }

    "return HTTP 401 unauthorised without an authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authenticated = false, jsonBody = Some(sampleReview), POST)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "GET /reviews" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authenticated = true, jsonBody = Some(sampleReview), POST)
        result <- makeSimpleRequest("reviews", authenticated = true, jsonBody = None, GET)
      } yield result

      validateResult(futureResult, OK, "title")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authenticated = false, jsonBody = None, GET)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

}
