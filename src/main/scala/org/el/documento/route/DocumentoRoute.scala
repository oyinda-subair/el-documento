package org.el.documento.route

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.el.documento.controller.DocumentoController
import org.el.documento.messages.{CreateRoleRequest, CreateUserRequest}

class DocumentoRoute(controller: DocumentoController) extends PlayJsonSupport {

  val version = "v1"
  val user = "user"
  val role = "role"
  val api = "api"

  protected val createUser: Route =
    path(api / version / "users") {
      post {
        entity(as[CreateUserRequest]) { request =>
          complete(StatusCodes.Created, controller.createUser(request))
        }
      }
    }

  protected val createRole: Route =
    path(api / version / "roles") {
      post {
        entity(as[CreateRoleRequest]) { request =>
          complete(StatusCodes.Created, controller.createRole(request))
        }
      }
    }


  val routes: Route =
    createUser ~
    createRole
}
