package services

import play.api.mvc.Cookie

class AuthService {

  def login(username: String, password: String): Option[Cookie] = ???

}
