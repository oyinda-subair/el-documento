package org.el.documento.config.http

import org.el.documento.config.ApplicationConfig
import org.el.documento.model.UserClaim
import pdi.jwt.{Jwt, JwtAlgorithm}
import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

trait JWTAuthenticationServices {
  def generateToken(userClaim: UserClaim) : String
  def verifyToken(token : String) : Try[(String,String,String)]
  def verifyJwtToken(token : String) : Option[UserClaim]
}

class JWTAuthentication extends ApplicationConfig with JWTAuthenticationServices {
  override def generateToken(userClaim: UserClaim): String = {
    try {
      Jwt.encode(Json.toJson(userClaim).toString(), secret, JwtAlgorithm.HS256)
    }catch {
      case e: Exception =>
        logger.error("Error encoding token")
        throw e
    }
  }
  override def verifyToken(token : String): Try[(String,String,String)] = Jwt.decodeRawAll(token, secret, Seq(JwtAlgorithm.HS256))

  override def verifyJwtToken(token: String): Option[UserClaim] = {
    val jwt = Jwt.decode(token, secret, Seq(JwtAlgorithm.HS256))
    jwt match {
      case Success(x) => Json.parse(x.content).asOpt[UserClaim]
      case Failure(exception) =>
        logger.error("Error decoding token")
        None
    }
  }
}
