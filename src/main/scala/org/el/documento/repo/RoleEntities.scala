package org.el.documento.repo

import org.el.documento.database.DatabaseConnector
import org.el.documento.messages.CreateRoleRequest
import org.el.documento.model.RoleEntity
import org.joda.time.DateTime
import slick.ast.ColumnOption.AutoInc

import scala.concurrent.{ExecutionContext, Future}

trait RoleEntities extends DatabaseConnector {
  import driver.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  class RoleTable(tag: Tag) extends Table[RoleEntity](tag, "role_by_id") {
    def id: Rep[Int] = column[Int]("role_id", O.PrimaryKey, AutoInc)

    def title: Rep[String] = column[String]("title", O.Unique)
    val index1 = index("index_title", title)

    def roleType: Rep[Option[String]] = column[Option[String]]("role_type")

    def timestampCreated: Rep[DateTime] = column[DateTime]("timestamp_created")

    def timestampUpdated: Rep[Option[DateTime]] = column[Option[DateTime]]("timestamp_updated")

    def * = (id, title, roleType, timestampCreated, timestampUpdated) <> ((RoleEntity.apply _).tupled, RoleEntity.unapply)
  }

  class RoleRepository(implicit ec: ExecutionContext) {
    val roles = TableQuery[RoleTable]

    def create(entity: CreateRoleRequest): Future[RoleEntity] = {
      val now = DateTime.now

      db.run {
        (roles.map(r ⇒ (r.title, r.roleType, r.timestampCreated))
          returning roles.map(_.id)
          into ((role, roleId) ⇒ RoleEntity(roleId, role._1, role._2, role._3, None))
          ) += (entity.title, entity.roleType, now)
      }
    }

    def getRoleById(id: Int): Future[Option[RoleEntity]] = db.run {
      roles.filter(_.id === id).result.headOption
    }

    def getRoleByTitle(title: String): Future[Option[RoleEntity]] = db.run{
      roles.filter(_.title === title).result.headOption
    }

    def deleteAll(): Future[Int] = db.run(roles.delete)

  }

}
