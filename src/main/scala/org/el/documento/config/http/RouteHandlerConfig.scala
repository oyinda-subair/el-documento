package org.el.documento.config.http

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import org.el.documento.config.{ApplicationConfig, ErrorResponse}

trait RouteHandlerConfig extends ApplicationConfig{
  def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MissingQueryParamRejection(param) =>
        val errorResponse = ErrorResponse(BadRequest.intValue, "Missing Parameter", s"The required $param was not found.").toStrEntity
        complete(HttpResponse(BadRequest, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
      }
      .handle { case AuthorizationFailedRejection =>
        val errorResponse = ErrorResponse(BadRequest.intValue, "Authorization", "The authorization check failed for you. Access Denied.").toStrEntity
        complete(HttpResponse(BadRequest, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
      }
      .handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        val errorResponse = ErrorResponse(MethodNotAllowed.intValue, "Not Allowed", s"Access to $names is not allowed.").toStrEntity
        complete(HttpResponse(MethodNotAllowed, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
      }
      .handleNotFound {
        val errorResponse = ErrorResponse(NotFound.intValue, "NotFound", "The requested resource could not be found.").toStrEntity
        complete(HttpResponse(NotFound, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
      }
      .result()

  def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: Exception =>
        extractUri { uri =>
          val errorResponse = ErrorResponse(InternalServerError.intValue, "Internal Server Error", "Error processing request").toStrEntity
          logger.error(s"Request to $uri could not be handled normally")
          complete(HttpResponse(InternalServerError, entity = errorResponse))
        }
    }
}
