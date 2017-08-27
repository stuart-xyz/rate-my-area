package controllers

import play.api.mvc._
import services.UserAuthAction

class IndexController(cc: ControllerComponents, userAuthAction: UserAuthAction)
  extends AbstractController(cc) {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def listUsers = userAuthAction {
    Ok(views.html.index())
  }

}
