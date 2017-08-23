package models

import play.api.libs.json.{Json, OWrites}
import slick.jdbc.PostgresProfile.api._

case class User(id: Int,
                email: String,
                password: String,
                fullName: String,
                isAdmin: Boolean)

object User {

  implicit val writes: OWrites[User] = Json.writes[User]

}

class UserTable(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Int]("id", O.PrimaryKey)
  def email = column[String]("email")
  def password = column[String]("password")
  def fullName = column[String]("fullname")
  def isAdmin = column[Boolean]("isadmin")

  override def * = (id, email, password, fullName, isAdmin) <> ((User.apply _).tupled, User.unapply)
}
