package models

import play.api.libs.json.{Json, OWrites}
import services.MyPostgresProfile.api._

case class Review(id: Int,
                  title: String,
                  areaName: String,
                  description: String,
                  imageUrls: List[String],
                  userId: Int)

object Review {

  implicit val reviewWrites: OWrites[Review] = Json.writes[Review]

}

class ReviewTable(tag: Tag) extends Table[Review](tag, "reviews") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def title = column[String]("title")
  def areaName = column[String]("area_name")
  def description = column[String]("description")
  def imageUrls = column[List[String]]("image_urls")
  def userId = column[Int]("user_id")

  override def * = (id, title, areaName, description, imageUrls, userId) <> ((Review.apply _).tupled, Review.unapply)
}
