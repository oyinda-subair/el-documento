package org.el.documento

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
//import io.sentry.{Sentry, SentryClient}
import org.el.documento.config.ApplicationConfig
import org.el.documento.config.http.RouteHandlerConfig
import org.el.documento.controller.{DocumentoController, DocumentoControllerImpl}
import org.el.documento.database.ElDocumentoDAO
import org.el.documento.route.DocumentoRoute

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}

object ServiceMain extends PlayJsonSupport with ApplicationConfig with App with RouteHandlerConfig {

  implicit val system: ActorSystem = ActorSystem("el-documento")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

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
