package org.el.documento.config

import play.api.libs.json.{Format, Json}

case class ErrorResponse(code: Int, `type`: String, message: String) {
  def toStrEntity = Json.toJson(this).toString()
}

object ErrorResponse {
  implicit val format: Format[ErrorResponse] = Json.format
}
