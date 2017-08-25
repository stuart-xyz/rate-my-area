package models

import play.api.libs.json.{Json, OWrites}
import slick.jdbc.PostgresProfile.api._

case class User(id: Int,
                email: String,
                hashedPassword: String,
                salt: String,
                fullName: String,
                isAdmin: Boolean)

object User {

  implicit val writes: OWrites[User] = Json.writes[User]

}

class UserTable(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def email = column[String]("email")
  def hashedPassword = column[String]("password")
  def salt = column[String]("salt")
  def fullName = column[String]("fullname")
  def isAdmin = column[Boolean]("isadmin")

  override def * = (id, email, hashedPassword, salt, fullName, isAdmin) <> ((User.apply _).tupled, User.unapply)
}
