package org.el.documento.route

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.el.documento.config.http.{BasicAuthentication, JWTAuthenticationServices}
import org.el.documento.controller.DocumentoController
import org.el.documento.messages.{CreateRoleRequest, CreateUserRequest, LoginByEmail}

import scala.concurrent.ExecutionContextExecutor

class DocumentoRoute(controller: DocumentoController)(implicit val jwtAuth: JWTAuthenticationServices, val ec: ExecutionContextExecutor) extends PlayJsonSupport with BasicAuthentication {

  val version = "v1"
  val user = "user"
  val role = "role"
  val api = "api"

  protected val createUser: Route =
    path(api / version / "users") {
      post {
        entity(as[CreateUserRequest]) { request =>
          complete((StatusCodes.Created, controller.createUser(request)))
        }
      }
    }

  protected val createRole: Route =
    path(api / version / "roles") {
      post {
        withSuperAdminAuthentication { isAdmin =>
          entity(as[CreateRoleRequest]) { request =>
            complete((StatusCodes.Created, controller.createRole(request)))
          }
        }
      }
    }

  protected val loginByEmail: Route =
    path(api / version / "login") {
      post {
        entity(as[LoginByEmail]) { request =>
          complete((StatusCodes.OK, controller.loginByEmail(request)))
        }
      }
    }


  val routes: Route =
    createUser ~
    createRole ~
    loginByEmail
}
