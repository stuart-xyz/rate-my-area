package services

import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.{Base64, UUID}

import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import play.api.mvc.{Cookie, RequestHeader}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

class AuthService(cacheApi: SyncCacheApi, databaseService: DatabaseService)(implicit ec: ExecutionContext) {

  case class HashedPasswordWithSalt(hashedPassword: String, salt: String)
  private val mda = MessageDigest.getInstance("SHA-512")
  private val cookieHeader = "X-Auth-Token"

  def login(email: String, password: String): Future[Option[Cookie]] = {
    for {
      userOption <- databaseService.getUserOption(email)
    } yield for {
      user <- userOption
      if BCrypt.checkpw(password, user.hashedPassword)
    } yield generateCookie(user)
  }

  def signup(email: String, password: String): Future[Int] = {
    val hashedPasswordWithSalt = hashPasswordWithSalt(password)
    databaseService.addUser(email, hashedPasswordWithSalt.hashedPassword, hashedPasswordWithSalt.salt)
  }

  def hashPasswordWithSalt(password: String): HashedPasswordWithSalt = {
    val salt = BCrypt.gensalt()
    val hashedPassword = BCrypt.hashpw(password, salt)
    HashedPasswordWithSalt(hashedPassword, salt)
  }

  def checkCookie(header: RequestHeader): Option[User] = {
    header.cookies.get(cookieHeader).flatMap(cookie => cacheApi.get[User](cookie.value))
  }

  private def generateCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.id.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }

}
