package org.el.documento.model

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class UserId(userId: UUID)

object UserId {
  implicit  val format: Format[UserId] = Json.format
}

case class UserClaim(userId: UUID, roleId: Int)

object UserClaim {
  implicit  val format: Format[UserClaim] = Json.format
}