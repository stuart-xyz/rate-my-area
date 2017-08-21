package controllers

import javax.inject._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

case class UserData(name: String, age: Int)

// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /user        controllers.UserController.userGet
POST    /user        controllers.UserController.userPost
*/

/**
 * User form controller for Play Scala
 */
class UserController @Inject()(mcc: MessagesControllerComponents) extends MessagesAbstractController(mcc) {

  val userForm = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(UserData.apply)(UserData.unapply)
  )

  def userGet() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.form(userForm))
  }

  def userPost() = Action { implicit request: MessagesRequest[AnyContent] =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.user.form(formWithErrors))
      },
      userData => {
        /* binding success, you get the actual value. */       
        /* flashing uses a short lived cookie */ 
        Redirect(routes.UserController.userGet()).flashing("success" -> ("Successful " + userData.toString))
      }
    )
  }
}
