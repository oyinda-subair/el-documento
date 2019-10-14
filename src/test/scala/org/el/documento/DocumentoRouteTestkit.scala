package org.el.documento

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives.{handleExceptions, handleRejections}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.el.documento.config.ApplicationConfig
import org.el.documento.config.http.RouteHandlerConfig
import org.el.documento.controller.{DocumentoController, DocumentoControllerImpl}
import org.el.documento.database.ElDocumentoDAO
import org.el.documento.route.DocumentoRoute
import org.scalatest.WordSpec
import play.api.libs.json.{Json, Reads, Writes}

trait DocumentoRouteTestkit extends WordSpec with ScalatestRouteTest with ApplicationConfig with RouteHandlerConfig with PlayJsonSupport {

  val db = new ElDocumentoDAO
  val version = "v1"
  val userPath = "user"
  val rolePath = "role"
  val api = "api"

  def toEntity[T: Reads: Writes](body: T): HttpEntity.Strict = {
    val message = Json.toJson(body).toString()
    HttpEntity(MediaTypes.`application/json`, message)
  }

  lazy val controller: DocumentoController = {
    new DocumentoControllerImpl(db)
  }

  lazy val documentoRoute = new DocumentoRoute(controller)

  val route: Route = {
    handleExceptions(myExceptionHandler) {
      handleRejections(rejectionHandler){
        documentoRoute.routes
      }
    }
  }
}
