package org.el.documento.messages

import play.api.libs.json.{Format, Json}

case class CreateRoleRequest(title: String, roleType: Option[String])

object CreateRoleRequest {
  implicit  val format: Format[CreateRoleRequest] = Json.format
}