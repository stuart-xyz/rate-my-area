package models

import play.api.libs.json.{Json, Writes}

case class DisplayedReview(review: Review,
                           username: String,
                           imageUrls: Seq[String])

object DisplayedReview {

  implicit val writes: Writes[DisplayedReview] =
    (displayedReview: DisplayedReview) => {
      val review = displayedReview.review
      val username = displayedReview.username
      val imageUrls = displayedReview.imageUrls
      Json.obj(
        "id" -> review.id,
        "title" -> review.title,
        "areaName" -> review.areaName,
        "description" -> review.description,
        "userId" -> review.userId,
        "username" -> username,
        "imageUrls" -> imageUrls
      )
    }

}
