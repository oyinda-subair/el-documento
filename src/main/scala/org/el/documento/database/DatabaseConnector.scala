package org.el.documento.database

import akka.stream.alpakka.slick.javadsl.SlickSession
import org.el.documento.config.ApplicationConfig
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DatabaseConnector extends ApplicationConfig {

  val driver: JdbcProfile = slick.jdbc.PostgresProfile

  import driver.api._

  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")

  val db = databaseConfig.db
  implicit val session: Session = db.createSession()
}
