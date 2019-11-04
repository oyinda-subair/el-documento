package org.el.documento.controller

import java.util.UUID

import akka.Done
import akka.actor.ActorSystem
import org.el.documento.config.base.SecureHelper.confirmPassword
import org.el.documento.config.ApplicationConfig
import org.el.documento.config.exceptions.{ResourceNotFoundException, UnauthorizedUserException}
import org.el.documento.config.http.JWTAuthenticationServices
import org.el.documento.database.ElDocumentoDAO
import org.el.documento.messages.{CreateRoleRequest, CreateUserRequest, LoginByEmail}
import org.el.documento.model._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DocumentoController {
  def createUser(request: CreateUserRequest): Future[UserToken]
  def createRole(request: CreateRoleRequest): Future[Done]
  def loginByEmail(request: LoginByEmail): Future[UserToken]
}

class DocumentoControllerImpl(documentoDb: ElDocumentoDAO)(implicit val system: ActorSystem, jwt: JWTAuthenticationServices) extends ApplicationConfig with DocumentoController {

  // User commands

  def createUser(request: CreateUserRequest): Future[UserToken] = {
    for {
      role <- getRoleByTitle(Public.name)
      entity <- documentoDb.UserRepo.create(request, role.roleId)
      role <- getRoleById(entity._2)
    } yield {
      val userClaim = UserClaim(entity._1, role.title)
      val token = jwt.generateToken(userClaim)
      UserToken(token).bearerToken
    }
  }

  // Role commands

  def createRole(request: CreateRoleRequest): Future[Done] = {
    for {
      _ <- documentoDb.RoleRepo.create(request)
    } yield Done
  }

  // Login Commands

  override def loginByEmail(request: LoginByEmail): Future[UserToken] = {
    for {
      user <- getUserByEmail(request.email)
      role <- getRoleById(user.roleId)
    } yield {
      if(confirmPassword(request.password, user.password)) {
        val token = jwt.generateToken(UserClaim(user.userId, role.title))
        UserToken(token).bearerToken
      } else {
        logger.error("Incorrect Password")
        throw UnauthorizedUserException("Incorrect password")
      }
    }
  }

  private def getUserByEmail(email: String): Future[UserEntity] = {
    documentoDb.UserRepo.getUserByEmail(email).map {
      case Some(user) => user
      case None =>
        logger.error("Email does not exist")
        throw ResourceNotFoundException("Email does not exist")
    }
  }

  private def getUserById(userId: UUID): Future[UserEntity] = {
    documentoDb.UserRepo.getUserById(userId).map {
      case Some(user) => user
      case None =>
        logger.error("Oops! sorry, UserId does not exist")
        throw ResourceNotFoundException("Oops! sorry, UserId does not exist")
    }
  }

  private def getRoleById(roleId: Int): Future[RoleEntity] = {
    documentoDb.RoleRepo.getRoleById(roleId).map {
      case Some(role) => role
      case None =>
        logger.error("Oops! sorry, RoleId does not exist")
        throw ResourceNotFoundException("Oops! sorry, RoleId does not exist")
    }
  }

  private def getRoleByTitle(title: String): Future[RoleEntity] = {
    documentoDb.RoleRepo.getRoleByTitle(title).map {
      case Some(role) => role
      case None =>
        logger.error("Oops! sorry, Role title does not exist")
        throw ResourceNotFoundException("Oops! sorry, Role title does not exist")
    }
  }
}
