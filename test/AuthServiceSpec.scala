import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.cache.SyncCacheApi
import services.{AuthService, DatabaseService}

import scala.concurrent.ExecutionContext.Implicits.global

class AuthServiceSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory with MockitoSugar {

  val mockCacheApi: SyncCacheApi = mock[SyncCacheApi]
  val mockDatabaseService: DatabaseService = mock[DatabaseService]
  val mockAuthService = new AuthService(mockCacheApi, mockDatabaseService)

  "AuthService#hashPasswordWithSalt" should {

    "return a unique password hash each time it is called" in {
      val password = "password"
      val hashedPasswords = (1 to 10).map(_ => mockAuthService.hashPasswordWithSalt(password).hashedPassword)
      hashedPasswords.distinct.size == hashedPasswords.size mustBe true
    }

  }

}
