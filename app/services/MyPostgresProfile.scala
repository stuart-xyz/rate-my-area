package services

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}

trait MyPostgresProfile extends ExPostgresProfile with PgArraySupport {

  def pgjson = "jsonb"
  override val api: Api.type = Api
  object Api extends API with ArrayImplicits {
    implicit val strListTypeMapper: DriverJdbcType[List[String]] = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val playJsonArrayTypeMapper: DriverJdbcType[List[JsValue]] =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
  }

}

object MyPostgresProfile extends MyPostgresProfile
