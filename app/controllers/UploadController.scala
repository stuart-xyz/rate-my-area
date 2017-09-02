package controllers

import java.nio.file.Paths

import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, ControllerComponents, MultipartFormData}
import services.UserAuthAction

import scala.concurrent.ExecutionContext

class UploadController(cc: ControllerComponents, userAuthAction: UserAuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = userAuthAction(parse.multipartFormData) { implicit request =>
    request.body.file("photo").map { photo =>
      val filename = photo.filename
      photo.ref.moveTo(Paths.get(s"/public/images/${request.user.id}/$filename"), replace = true)
      Ok(Json.obj("message" -> "File uploaded", "fileName" -> filename))
    }.getOrElse(BadRequest(Json.obj("error" -> "Missing file")))
  }

}
