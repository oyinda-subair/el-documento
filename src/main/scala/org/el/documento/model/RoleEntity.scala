package org.el.documento.model

import org.el.documento.config.base.FormatEntity
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class RoleEntity(
                       roleId: Int,
                       title: String,
                       roleType: Option[String],
                       timestampCreated: DateTime,
                       timestampUpdated: Option[DateTime]
                     )

object RoleEntity extends FormatEntity[RoleEntity]{
  implicit  val format: Format[RoleEntity] = Json.format
}

sealed abstract class RoleType(val name: String)

case object Public extends RoleType("public")
case object Admin extends RoleType("admin")
case object SuperAdmin extends RoleType("super-admin")