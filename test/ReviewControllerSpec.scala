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

  val sampleReviewEdit: JsObject = Json.obj(
    "title" -> "updated-title"
  )

  "POST /reviews" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
      validateResult(result, OK, "message")
    }

    "return HTTP 401 unauthorised without an authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authCookieOption = None, jsonBody = Some(sampleReview), POST)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "GET /reviews" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      validateResult(futureResult, OK, "title")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authCookieOption = None, jsonBody = None, GET)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "PATCH /reviews/:id" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
        result <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReviewEdit), PATCH)
      } yield result

      validateResult(futureResult, OK, "message")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("reviews/1", authCookieOption = None, jsonBody = Some(sampleReviewEdit), PATCH)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "DELETE /reviews/:id" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
        result <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = None, DELETE)
      } yield result

      validateResult(futureResult, OK, "message")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("reviews/1", authCookieOption = None, jsonBody = Some(sampleReviewEdit), DELETE)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }


  it should {

    "store a new review" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      (contentAsJson(futureResult) \\ "title").head.as[String] == (sampleReview \ "title").as[String] mustBe true
    }

    "edit a stored review" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
        _ <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReviewEdit), PATCH)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      (contentAsJson(futureResult) \\ "title").head.as[String] == (sampleReviewEdit \ "title").as[String] mustBe true
    }

    "delete a stored review" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReview), POST)
        _ <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(sampleReviewEdit), DELETE)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      (contentAsJson(futureResult) \\ "title").isEmpty mustBe true
    }

  }

}
