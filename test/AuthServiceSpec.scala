import models.User
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.cache.SyncCacheApi
import play.api.test.FakeRequest
import services.{AuthService, DatabaseService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

class AuthServiceSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory with MockitoSugar {

  val mockUser = User(1, "email@email.com", "hashed-password", "salt", "username")
  val mockDatabaseService: DatabaseService = mock[DatabaseService]
  val mockAuthService = new AuthService(mockDatabaseService, fakeApplication().configuration)

  "AuthService#hashPasswordWithSalt" should {

    "return a unique password hash each time it is called" in {
      val password = "password"
      val hashedPasswords = (1 to 10).map(_ => mockAuthService.hashPasswordWithSalt(password).hashedPassword)
      hashedPasswords.distinct.size == hashedPasswords.size mustBe true
    }

  }

  "AuthService#generateCookie" should {

    "generate and verify JWT cookies" in {
      val cookie = mockAuthService.generateJWTCookie(mockUser)
      mockAuthService.checkJWT(FakeRequest().withCookies(cookie)).isDefined mustBe true
    }

  }

}
