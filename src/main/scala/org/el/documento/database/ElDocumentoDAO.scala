package org.el.documento.database

import org.el.documento.repo.{RoleEntities, UserEntities}

import scala.concurrent.ExecutionContext

class ElDocumentoDAO(implicit ec: ExecutionContext) extends UserEntities with RoleEntities with DatabaseConnector {
  object UserRepo extends UserRepository
  object RoleRepo extends RoleRepository
}
