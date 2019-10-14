package org.el.documento.config.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{handleExceptions, handleRejections}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.el.documento.ServiceMain.{myExceptionHandler, rejectionHandler}

import scala.concurrent.ExecutionContextExecutor

trait DocumentoDirectives {

//  implicit val system: ActorSystem = ActorSystem("el-documento")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route: Route

  lazy val routeWithHandler: Route =
  {
    handleExceptions(myExceptionHandler) {
      handleRejections(rejectionHandler){
        route
      }
    }
  }
}
