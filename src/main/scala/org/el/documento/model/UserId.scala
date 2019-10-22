package org.el.documento.model

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class UserId(userId: UUID)

object UserId {
  implicit  val format: Format[UserId] = Json.format
}

case class UserClaim(userId: UUID, roleTitle: String)

object UserClaim {
  implicit  val format: Format[UserClaim] = Json.format
}

case class UserToken(token: String) {
  def bearerToken = UserToken(s"Bearer $token")
}

object UserToken {
  implicit val format: Format[UserToken] = Json.format
}