package models

import play.api.libs.json.{Json, OWrites}
import slick.jdbc.PostgresProfile.api._

case class User(id: Int,
                email: String,
                hashedPassword: String,
                salt: String,
                username: String)

object User {

  implicit val writes: OWrites[User] = Json.writes[User]

}

class UserTable(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def email = column[String]("email")
  def hashedPassword = column[String]("password")
  def salt = column[String]("salt")
  def username = column[String]("username")

  override def * = (id, email, hashedPassword, salt, username) <> ((User.apply _).tupled, User.unapply)
}
