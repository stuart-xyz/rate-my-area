package models

import play.api.libs.json.{Json, Writes}

case class ReviewWithImageUrls(review: Review,
                               imageUrls: Seq[String])

object ReviewWithImageUrls {

  implicit val writes: Writes[ReviewWithImageUrls] =
    (reviewWithImageUrls: ReviewWithImageUrls) => {
      val review = reviewWithImageUrls.review
      val imageUrls = reviewWithImageUrls.imageUrls
      Json.obj(
        "id" -> review.id,
        "title" -> review.title,
        "areaName" -> review.areaName,
        "description" -> review.description,
        "userId" -> review.userId,
        "imageUrls" -> imageUrls
      )
    }

}
