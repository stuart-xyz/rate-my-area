package controllers

import java.util.UUID

import akka.actor.ActorSystem
import play.api.libs.json.Json
import play.api.mvc._
import services.{S3Service, UserAuthAction}

import scala.concurrent.{ExecutionContext, Future}

class UploadController(cc: ControllerComponents, userAuthAction: UserAuthAction, s3Service: S3Service)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val akkaSystem = ActorSystem("ratemyarea")
  val executionContext: ExecutionContext = akkaSystem.dispatchers.lookup("upload-context")

  def upload: Action[AnyContent] = userAuthAction.async { implicit request =>
    Future {
      val urls = request.body.asMultipartFormData.get.files.map(file => {
        val filename = UUID.randomUUID()
        s3Service.upload(file.ref.toFile, filename.toString, request.user.id)
      })
      Ok(Json.obj("message" -> "Files uploaded", "urls" -> urls))
    }(executionContext)
  }

}
