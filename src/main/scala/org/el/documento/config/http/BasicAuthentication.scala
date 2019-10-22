package org.el.documento.config.http

import java.util.UUID

import akka.http.scaladsl.server._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import org.el.documento.config.ApplicationConfig
import org.el.documento.model.{Public, SuperAdmin, UserClaim}

import scala.concurrent.ExecutionContextExecutor

trait BasicAuthentication extends ApplicationConfig {

  implicit val jwtAuth: JWTAuthenticationServices
  implicit val ec: ExecutionContextExecutor

  private def getUserId(userClaim: UserClaim): UUID = userClaim.userId

  private def getUserWithRoleId(userClaim: UserClaim): (UUID, String) = (userClaim.userId, userClaim.roleTitle)

  def authenticate(token: String): Directive1[UserClaim] = {
    jwtAuth.verifyJwtToken(token) match {
      case Some(user) => provide(user)
      case None =>
        logger.error("Invalid authorization credential type")
        reject(AuthorizationFailedRejection)
    }
  }

  def authenticatedWithHeader: Directive1[UserClaim] = {
    extractCredentials.flatMap {
      case Some(OAuth2BearerToken(token))  => authenticate(token)
      case _ =>
        logger.error("Invalid authorization credential type")
        reject(AuthorizationFailedRejection)
    }
  }

  def withPublicAuthentication: Directive1[UUID] = {
    extraToken.flatMap {
      case userClaim: UserClaim if isPublic(userClaim) => provide(userClaim.userId)
      case _ =>
        logger.error("User does not have access to this route")
        reject(AuthorizationFailedRejection)
    }
  }

  def withSuperAdminAuthentication: Directive1[UUID] = {
    extraToken.flatMap {
      case userClaim: UserClaim if isSuperAdmin(userClaim) => provide(userClaim.userId)
      case _ =>
        logger.error("User does not have access to this route")
        reject(AuthorizationFailedRejection)
    }
  }

  private def isPublic(userClaim: UserClaim): Boolean = Public.name.toLowerCase().contains(userClaim.roleTitle)
  private def isSuperAdmin(userClaim: UserClaim): Boolean = SuperAdmin.name.toLowerCase().contains(userClaim.roleTitle)

  private def extraToken: Directive1[UserClaim] = {
    extractCredentials.flatMap {
      case Some(OAuth2BearerToken(token))  => authenticate(token)
      case _ =>
        logger.error("Invalid authorization credential type")
        reject(AuthorizationFailedRejection)
    }
  }
}
