package org.el.documento.model

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class UserId(user_id: UUID)

object UserId {
  implicit  val format: Format[UserId] = Json.format
}

case class UserClaim(user_id: UUID, roleId: Int)

object UserClaim {
  implicit  val format: Format[UserClaim] = Json.format
}