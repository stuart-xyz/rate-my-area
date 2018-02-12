package services

import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.{Base64, Calendar, TimeZone, UUID}

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.Configuration
import play.api.cache.SyncCacheApi
import play.api.mvc.{Cookie, RequestHeader}
import services.CustomExceptions.UserNotLoggedInException

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class AuthService(cacheApi: SyncCacheApi, databaseService: DatabaseService, appConfig: Configuration)(implicit ec: ExecutionContext) {

  case class HashedPasswordWithSalt(hashedPassword: String, salt: String)
  private val mda = MessageDigest.getInstance("SHA-512")
  private val cookieHeader = "X-Auth-Token"
  private val algorithm = Algorithm.HMAC256(appConfig.get[String]("play.http.secret.key"))

  def login(email: String, password: String): Future[Try[Option[Cookie]]] = {
    for {
      userOptionTry <- databaseService.getUserOption(email)
    } yield userOptionTry match {
      case Success(userOption) =>
        val cookieOption = userOption.flatMap(user => {
          if (BCrypt.checkpw(password, user.hashedPassword)) Some(generateJWTCookie(user))
          else None
        })
        Success(cookieOption)
      case Failure(e) => Failure(e)
    }
  }

  def signup(email: String, username: String, password: String): Future[Try[Int]] = {
    val hashedPasswordWithSalt = hashPasswordWithSalt(password)
    databaseService.addUser(email, username, hashedPasswordWithSalt.hashedPassword, hashedPasswordWithSalt.salt)
  }

  def logout(header: RequestHeader): Option[Try[Unit]] = {
    header.cookies.get(cookieHeader).map(cookie => {
      if (cacheApi.get[User](cookie.value).isDefined) Success(cacheApi.remove(cookie.value))
      else Failure(new UserNotLoggedInException)
    })
  }

  def hashPasswordWithSalt(password: String): HashedPasswordWithSalt = {
    val salt = BCrypt.gensalt()
    val hashedPassword = BCrypt.hashpw(password, salt)
    HashedPasswordWithSalt(hashedPassword, salt)
  }

  def checkCookie(header: RequestHeader): Option[User] = {
    header.cookies.get(cookieHeader).flatMap(cookie => cacheApi.get[User](cookie.value))
  }

  def generateCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.id.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }

  def checkJWT(header: RequestHeader): Option[User] = {
    val tokenOption = header.cookies.get(cookieHeader).map(_.value)
    tokenOption.flatMap(token => {
      val verifier = JWT.require(algorithm).build()
      Try(verifier.verify(token)) match {
        case Success(decoded) => Some(User(decoded.getClaim("id").asString.toInt, decoded.getClaim("email").asString,
            null, null, decoded.getClaim("username").asString))
        case Failure(_) => None
      }
    })
  }

  def generateJWTCookie(user: User): Cookie = {
    val nowPlusTenHours = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    nowPlusTenHours.add(Calendar.HOUR, 10)
    val token = JWT.create
      .withClaim("id", user.id.toString)
      .withClaim("username", user.username)
      .withClaim("email", user.email)
      .withExpiresAt(nowPlusTenHours.getTime)
      .sign(algorithm)
    val duration = Duration.create(10, TimeUnit.HOURS)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }

}
