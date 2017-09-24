import java.io.File

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import services.S3Service

import scala.concurrent.ExecutionContext.Implicits.global

class ReviewControllerSpec extends PlaySpec with AuthenticatedUser with TestHelpers with BaseOneAppPerTest with AppApplicationFactory with MockitoSugar {

  val mockS3Service: S3Service = mock[S3Service]
  when(mockS3Service.upload(any[File], any[String], any[Int])) thenReturn "test"
  override def fakeApplication(): Application = new AppApplicationBuilder().build(Some(mockS3Service))

  val review: JsObject = Json.obj(
    "title" -> "title",
    "areaName" -> "area",
    "description" -> "description",
    "imageUrls" -> Json.arr("https://image.user-content.ratemyarea.stuartp.io")
  )

  val invalidReviewList = List(
    review ++ Json.obj("title" -> ""),
    review ++ Json.obj("areaName" -> ""),
    review ++ Json.obj("description" -> "")
  )

  val reviewEdit: JsObject = Json.obj(
    "title" -> "updated-title"
  )

  val invalidReviewEditList = List(
    reviewEdit ++ Json.obj("title" -> ""),
    reviewEdit ++ Json.obj("areaName" -> ""),
    reviewEdit ++ Json.obj("description" -> "")
  )

  "POST /reviews" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
      validateResult(result, OK, "message")
    }

    "return HTTP 400 bad request with an invalid review format" in {
      val futureResults = for {
        invalidReview <- invalidReviewList
      } yield makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(invalidReview), POST)

      futureResults.foreach(futureResult => validateResult(futureResult, BAD_REQUEST, "error"))
    }

    "return HTTP 401 unauthorised without an authorisation cookie" in {
      val result = makeSimpleRequest("reviews", authCookieOption = None, jsonBody = Some(review), POST)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "GET /reviews" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
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
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        result <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(reviewEdit), PATCH)
      } yield result

      validateResult(futureResult, OK, "message")
    }

    "return HTTP 400 bad request with an invalid review edit format" in {
      makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST).map(_ => {
        val futureResults = for {
          invalidReviewEdit <- invalidReviewEditList
        } yield makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(invalidReviewEdit), PATCH)

        futureResults.foreach(futureResult => validateResult(futureResult, BAD_REQUEST, "error"))
      })
    }

    "return HTTP 404 not found with invalid ID" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        result <- makeSimpleRequest("reviews/2", authCookieOption = Some(getAuthCookie), jsonBody = Some(reviewEdit), PATCH)
      } yield result

      validateResult(futureResult, NOT_FOUND, "error")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("reviews/1", authCookieOption = None, jsonBody = Some(reviewEdit), PATCH)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "DELETE /reviews/:id" should {

    "return HTTP 200 ok with authorisation cookie" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        result <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = None, DELETE)
      } yield result

      validateResult(futureResult, OK, "message")
    }

    "return HTTP 404 not found with invalid ID" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        result <- makeSimpleRequest("reviews/2", authCookieOption = Some(getAuthCookie), jsonBody = None, DELETE)
      } yield result

      validateResult(futureResult, NOT_FOUND, "error")
    }

    "return HTTP 401 unauthorised without authorisation cookie" in {
      val result = makeSimpleRequest("reviews/1", authCookieOption = None, jsonBody = Some(reviewEdit), DELETE)
      validateResult(result, UNAUTHORIZED, "error")
    }

  }

  "ReviewController" should {

    "store a new review" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      (contentAsJson(futureResult) \\ "title").head.as[String] == (review \ "title").as[String] mustBe true
    }

    "edit a stored review" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        _ <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(reviewEdit), PATCH)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      (contentAsJson(futureResult) \\ "title").head.as[String] == (reviewEdit \ "title").as[String] mustBe true
    }

    "delete a stored review" in {
      val futureResult = for {
        _ <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = Some(review), POST)
        _ <- makeSimpleRequest("reviews/1", authCookieOption = Some(getAuthCookie), jsonBody = Some(reviewEdit), DELETE)
        result <- makeSimpleRequest("reviews", authCookieOption = Some(getAuthCookie), jsonBody = None, GET)
      } yield result

      (contentAsJson(futureResult) \\ "title").isEmpty mustBe true
    }

  }

}
