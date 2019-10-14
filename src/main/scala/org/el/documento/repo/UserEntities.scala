package org.el.documento.repo

import java.util.UUID

import org.el.documento.config.base.SecureHelper._
import org.el.documento.database.DatabaseConnector
import org.el.documento.messages.CreateUserRequest
import org.el.documento.model.{UserClaim, UserEntity}
import org.joda.time.DateTime
import slick.ast.ColumnOption.AutoInc

import scala.concurrent.{ExecutionContext, Future}

trait UserEntities extends RoleEntities with DatabaseConnector {
  import driver.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  val roleRepo: RoleRepository = {
    import scala.concurrent.ExecutionContext.Implicits.global
    new RoleRepository
  }
  val roles = roleRepo.roles

  class UserTable(tag: Tag) extends Table[UserEntity](tag, "user_by_id") {
    def id: Rep[UUID] = column[UUID]("user_id", O.PrimaryKey, AutoInc)

    def name: Rep[String] = column[String]("name")
    val index1 = index("index_name", name)

    def username: Rep[String] = column[String]("username", O.Unique)
    val index2 = index("index_username", username)

    def email: Rep[String] = column[String]("email", O.Unique)
    val index3 = index("index_email", email)

    def password: Rep[String] = column[String]("password")

    def roleId: Rep[Int] = column[Int]("role_id")

    def timestampCreated: Rep[DateTime] = column[DateTime]("timestamp_created")

    def timestampUpdated: Rep[Option[DateTime]] = column[Option[DateTime]]("timestamp_updated")

    def * = (id, name, username, email, password, roleId, timestampCreated, timestampUpdated) <> ((UserEntity.apply _).tupled, UserEntity.unapply)

    def role = foreignKey("role_by_id_fk", roleId, roles)(_.id, onDelete = ForeignKeyAction.Cascade)
  }

  class UserRepository(implicit ec: ExecutionContext) {
    val users = TableQuery[UserTable]

    def create(entity: CreateUserRequest, roleId: Int): Future[UserClaim] = {
      val now = DateTime.now
      val user_id = UUID.randomUUID()
      val password = hashPassword(entity.password)
      val user = UserEntity(user_id, entity.name, entity.username, entity.email, password, roleId, now, None)
      db.run(users returning users.map(u => (u.id, u.roleId)) += user).map(user => UserClaim(user._1, user._2))
    }

    def getAllUsers: Future[Seq[UserEntity]] =  db.run (users.result)

    def getUserByEmail(email: String): Future[Option[UserEntity]] = db.run {
      users.filter(_.email === email).result.headOption
    }

    def getUserById(userId: UUID): Future[Option[UserEntity]] = db.run {
      users.filter(_.id === userId).result.headOption
    }

    def deleteAll(): Future[Int] = db.run(users.delete)
  }
}
