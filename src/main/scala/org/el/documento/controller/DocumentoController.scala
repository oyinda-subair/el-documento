package org.el.documento.controller

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import org.el.documento.config.ApplicationConfig
import org.el.documento.database.ElDocumentoDAO
import org.el.documento.messages.{CreateRoleRequest, CreateUserRequest}
import org.el.documento.model.{Public, RoleEntity, UserClaim, UserEntity}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DocumentoController {
  def createUser(request: CreateUserRequest): Future[UserClaim]
  def createRole(request: CreateRoleRequest): Future[Done]
}

class DocumentoControllerImpl(documentoDb: ElDocumentoDAO)(implicit val system: ActorSystem) extends ApplicationConfig with DocumentoController {

  // User commands
  def createUser(request: CreateUserRequest): Future[UserClaim] = {
    for {
      role <- documentoDb.RoleRepo.getRoleByTitle(Public.name)
      _ = if(role.isEmpty)
        logger.error("Role does not exist", HttpResponse(404, entity = s"Unfortunately, the resource ${Public.name} couldn't be found."))
      entity <- documentoDb.UserRepo.create(request, role.get.roleId)
    } yield UserClaim(entity.userId, entity.roleId)
  }

  // Role commands

  def createRole(request: CreateRoleRequest): Future[Done] = {
    for {
      _ <- documentoDb.RoleRepo.create(request)
    } yield Done
  }
}
