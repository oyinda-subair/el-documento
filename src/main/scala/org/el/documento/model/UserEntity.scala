package org.el.documento.model

import java.util.UUID

import org.el.documento.config.base.FormatEntity
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class UserEntity(
                       userId: UUID,
                       name: String,
                       username: String,
                       email: String,
                       password: String,
                       roleId: Int,
                       timestampCreated: DateTime,
                       timestampUpdated: Option[DateTime]
                     )

object UserEntity extends FormatEntity[UserEntity]{
  implicit  val format: Format[UserEntity] = Json.format
}
