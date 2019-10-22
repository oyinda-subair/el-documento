package org.el.documento

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.el.documento.config.ApplicationConfig
import org.el.documento.config.http.{JWTAuthentication, JWTAuthenticationServices, RouteHandlerConfig}
import org.el.documento.controller.{DocumentoController, DocumentoControllerImpl}
import org.el.documento.database.ElDocumentoDAO
import org.el.documento.route.DocumentoRoute

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object ServiceMain extends PlayJsonSupport with ApplicationConfig with App with RouteHandlerConfig {

  implicit val system: ActorSystem = ActorSystem("el-documento")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val jwt: JWTAuthenticationServices = new JWTAuthentication

  lazy val controller: DocumentoController = {
    val db = new ElDocumentoDAO
    new DocumentoControllerImpl(db)
  }

  lazy val documentoRoute = new DocumentoRoute(controller)

  lazy val routeWithHandler: Route = {
    handleExceptions(myExceptionHandler) {
      handleRejections(rejectionHandler) {
        documentoRoute.routes
      }
    }
  }

  val binding = Http().bindAndHandle(routeWithHandler, httpInterface, httpPort)

  binding.onComplete {
    case Success(serverBinding) =>
      logger.info(s"==> Server bound to http:/${serverBinding.localAddress}")
    case Failure(ex) =>
      logger.error(s"Failed to bind to $httpInterface:$httpPort !", ex)
      system.terminate()
  }

}
