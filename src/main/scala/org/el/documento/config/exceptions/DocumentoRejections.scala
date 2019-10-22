package org.el.documento.config.exceptions

import akka.http.javadsl.server.Rejection

case class UnauthorizedUser(message: String) extends Rejection