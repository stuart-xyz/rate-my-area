package controllers

import java.util.UUID

import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, ControllerComponents, MultipartFormData}
import services.{UploadService, UserAuthAction}

import scala.concurrent.ExecutionContext

class UploadController(cc: ControllerComponents, userAuthAction: UserAuthAction, uploadService: UploadService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = userAuthAction(parse.multipartFormData) { implicit request =>
    val urls = request.body.files.map(file => {
      val filename = UUID.randomUUID()
      uploadService.upload(file.ref.toFile, filename.toString, request.user.id)
    })
    Ok(Json.obj("message" -> "Files uploaded", "urls" -> urls))
  }

}
