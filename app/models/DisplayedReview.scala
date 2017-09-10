package models

import play.api.libs.json._

case class DisplayedReview(review: Review,
                           username: String,
                           imageUrls: Seq[String])

object DisplayedReview {

  implicit val writes: Writes[DisplayedReview] = (displayedReview: DisplayedReview) => {
    Json.toJsObject(displayedReview.review) ++ Json.obj("username" -> displayedReview.username) ++
      Json.obj("imageUrls" -> displayedReview.imageUrls)
  }

}
