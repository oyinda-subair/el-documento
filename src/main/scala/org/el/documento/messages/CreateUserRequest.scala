package org.el.documento.messages

import org.el.documento.config.base.FormatEntity
import play.api.libs.json.{Format, Json}

case class CreateUserRequest(
                              name: String,
                              username: String,
                              email: String,
                              password: String,
                            )

object CreateUserRequest extends FormatEntity[CreateUserRequest]{
  implicit  val format: Format[CreateUserRequest] = Json.format
}