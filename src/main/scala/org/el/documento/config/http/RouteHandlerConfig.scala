package org.el.documento.config.http

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import org.el.documento.config.exceptions.{ResourceNotFoundException, UnauthorizedUserException}
import org.el.documento.config.{ApplicationConfig, ErrorResponse}

trait RouteHandlerConfig extends ApplicationConfig{
  def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MissingQueryParamRejection(param) =>
        val errorResponse = ErrorResponse(BadRequest.intValue, "Missing Parameter", s"The required $param was not found.").toStrEntity
        complete(HttpResponse(BadRequest, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
      }
      .handle { case AuthorizationFailedRejection =>
        val errorResponse = ErrorResponse(Unauthorized.intValue, "Authorization", "The authorization check failed for you. Access Denied.").toStrEntity
        complete(HttpResponse(Unauthorized, entity = HttpEntity(ContentTypes.`application/json`, errorResponse)))
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
      case e: ResourceNotFoundException => logException(NotFound, e.message, "Not Found Error")
      case e: UnauthorizedUserException => logException(Unauthorized, e.message, "Unauthorized Error")
      case _: Exception =>
        extractUri { uri =>
          val errorResponse = ErrorResponse(InternalServerError.intValue, "Internal Server Error", "Error processing request").toStrEntity
          logger.error(s"Request to $uri could not be handled normally")
          complete(HttpResponse(InternalServerError, entity = errorResponse))
        }
    }

  private def logException(code: StatusCode, message: String, errorType: String): StandardRoute = {
    val errorResponse = ErrorResponse(code.intValue(), errorType, message)
    val response = HttpResponse(code, entity = errorResponse.toStrEntity)
    logger.error(s"Error processing user request: $message")
    complete(response)
  }
}
