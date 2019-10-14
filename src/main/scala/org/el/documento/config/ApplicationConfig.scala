package org.el.documento.config

import com.typesafe.config.ConfigFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait ApplicationConfig {

  lazy val className: String = if(this.getClass.getCanonicalName != null)
    this.getClass.getCanonicalName else "none"

  private val config = ConfigFactory.load()
  private val databaseConfig = config.getConfig("db")
  private val httpConfig = config.getConfig("app")

  val pgDriver: String  = databaseConfig.getString("driver")
//  val pgUrl: String = databaseConfig.getString("url")
  val pgPassword: String = databaseConfig.getString("password")
  val pgUser: String = databaseConfig.getString("user")
  val pgHost: String = databaseConfig.getString("host")
  val pgDBName: String = databaseConfig.getString("name")

  val secret: String = System.getenv("JWT_SECRET")

  val logger: Logger = LoggerFactory.getLogger(className)

  val httpInterface: String = httpConfig.getString("host")
  val httpPort: Int = httpConfig.getInt("port")

  val env: String = httpConfig.getString("env")
}
