package models

import play.api.libs.json.{JsObject, Json}

case class Response(message: String, hasError: Boolean) {

  def json: JsObject = Json.obj(
    "errorHasOccurred" -> hasError,
    "message" -> message
  )

}
