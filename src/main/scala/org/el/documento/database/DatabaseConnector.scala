package org.el.documento.database

import org.el.documento.config.ApplicationConfig
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DatabaseConnector extends ApplicationConfig {

  val driver: JdbcProfile = slick.jdbc.PostgresProfile

  import driver.api._

  private val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")

  val db = databaseConfig.db
  implicit val session: Session = db.createSession()
}
