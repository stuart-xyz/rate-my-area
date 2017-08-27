package controllers

import play.api.mvc._

class IndexController(cc: ControllerComponents)
  extends AbstractController(cc) {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index())
  }

}
