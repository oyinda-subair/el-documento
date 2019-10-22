package org.el.documento.messages

import play.api.libs.json.{Format, Json}

case class LoginByEmail(email: String, password: String)

object LoginByEmail {
  implicit val format: Format[LoginByEmail] = Json.format
}
