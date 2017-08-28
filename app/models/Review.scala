package models

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.PostgresProfile.api._

case class Review(id: Int,
                  title: String,
                  areaName: String,
                  emojiCode: String,
                  description: String,
                  userId: Int)

object Review {

  implicit val reviewFormat: OFormat[Review] = Json.format[Review]

}

class ReviewTable(tag: Tag) extends Table[Review](tag, "reviews") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def title = column[String]("title")
  def areaName = column[String]("area_name")
  def emojiCode = column[String]("string")
  def description = column[String]("description")
  def userId = column[Int]("user_id")

  override def * = (id, title, areaName, emojiCode, description, userId) <> ((Review.apply _).tupled, Review.unapply)
}
