package org.el.documento.config.exceptions

case class ResourceNotFoundException(message: String, cause: Option[Throwable] = None) extends Exception(message, cause.orNull)

case class UnauthorizedUserException(message: String, cause: Option[Throwable] = None) extends Exception(message, cause.orNull)
