package controllers

import java.util.UUID

import play.api.libs.json.Json
import play.api.mvc._
import services.{S3Service, UserAuthAction}

import scala.concurrent.ExecutionContext

class UploadController(cc: ControllerComponents, userAuthAction: UserAuthAction, s3Service: S3Service)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def upload: Action[AnyContent] = userAuthAction { implicit request =>
    val urls = request.body.asMultipartFormData.get.files.map(file => {
      val filename = UUID.randomUUID()
      s3Service.upload(file.ref.toFile, filename.toString, request.user.id)
    })
    Ok(Json.obj("message" -> "Files uploaded", "urls" -> urls))
  }

}
