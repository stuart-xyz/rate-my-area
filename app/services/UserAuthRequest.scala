package services

import models.User
import play.api.mvc.{Request, WrappedRequest}

case class UserAuthRequest[A](user: User, request: Request[A])
  extends WrappedRequest[A](request)
