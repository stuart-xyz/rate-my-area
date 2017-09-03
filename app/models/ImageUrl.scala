package models

import slick.jdbc.PostgresProfile.api._

case class ImageUrl(id: Int,
                    url: String,
                    reviewId: Int)

class ImageUrlTable(tag: Tag) extends Table[ImageUrl](tag, "image_urls") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def url = column[String]("url")
  def reviewId = column[Int]("review_id")

  override def * = (id, url, reviewId) <> ((ImageUrl.apply _).tupled, ImageUrl.unapply)
}
