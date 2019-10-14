val akkaHttpVersion    = "10.1.9"
val akkaStreamVersion  = "2.5.23"
val akkaSlickVersion   = "1.1.1"
val playJsonVersion    = "2.7.3"
val flywayVersion      = "5.0.2"
val scalaTestVersion   = "3.0.8"
val akkaHttpSessionVersion = "0.5.10"
val pauldijouVersion = "4.0.0"
val log4jVersion     = "2.10.0"
val sentryVersion = "1.7.27"
val slickJodaMapperVersion = "2.4.0"
val qosVersion = "1.2.3"

val akkaHttp       = "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion
val akkaStream     = "com.typesafe.akka" %% "akka-stream"          % akkaStreamVersion
val playJson       = "com.typesafe.play" %% "play-json"            % playJsonVersion
val playJsonSupport = "de.heikoseeberger" %% "akka-http-play-json" % "1.27.0"
val bcyrpt          = "org.mindrot" % "jbcrypt" % "0.3m"

val akkaSlick      =  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % akkaSlickVersion
val postgres       = "org.postgresql" % "postgresql" % "42.2.6"
val flywayCore     = "org.flywaydb" % "flyway-core" % flywayVersion

val scalaTest      = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
val akkaStreamTest = "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test
val akkaTest       = "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test
val akkaHttpTest   = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

//val log4jApi       = "org.apache.logging.log4j"      %  "log4j-api"                % log4jVersion
//val log4jCore      = "org.apache.logging.log4j"      %  "log4j-core"               % log4jVersion
val slf4j          = "org.slf4j" % "slf4j-api" % "1.7.28"
val qos            = "ch.qos.logback" % "logback-classic" % qosVersion
val qosCore       = "ch.qos.logback" % "logback-core" % qosVersion
//val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
val sentryIO       = "io.sentry" % "sentry" % sentryVersion
val sentryIOLogback  = "io.sentry" % "sentry-logback" % sentryVersion
//val playLogback    = "com.typesafe.play" %% "play-logback" % "2.7.3"

val akkaHttpSessionCore = "com.softwaremill.akka-http-session" %% "core" % akkaHttpSessionVersion
val akkaHttpSessionJwt  = "com.softwaremill.akka-http-session" %% "jwt"  % akkaHttpSessionVersion

val jodaTime            = "joda-time" % "joda-time" % "2.10.3"
val jodaConvert         = "org.joda" % "joda-convert" % "2.2.1"
val jodaTimeFormatter   =  "com.github.tminglei" %% "slick-pg_joda-time" % "0.18.0"
val tototoshi           = "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperVersion

val pauldijouJwt        = "com.pauldijou" %% "jwt-core" % "4.0.0"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Xfatal-warnings"
  ),
  resolvers ++= Seq(
    "typesafe" at "https://repo.typesafe.com/typesafe/releases/",
    Resolver.jcenterRepo,
    "Artima Maven Repository" at "https://repo.artima.com/releases"
  )
)

lazy val root = (project in file("."))
  .enablePlugins(FlywayPlugin)
  .settings(
    name := "el-documento",
    version := "0.1",
    commonSettings,
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream,
      akkaSlick,
      akkaStreamTest,
      akkaHttpTest,
      bcyrpt,
      flywayCore,
      postgres,
      playJson,
      playJsonSupport,
      pauldijouJwt,
      slf4j,
      qos,
      qosCore,
      scalaTest,
      sentryIO,
      sentryIOLogback,
      jodaTime,
      jodaConvert,
      tototoshi
    )
  )

flywayUrl := "jdbc:postgresql://localhost:5432/el_documento"
flywayUser := "postgres"
flywayPassword := ""
flywayLocations += "db/migration"
flywayUrl in Test := "jdbc:postgresql://localhost:5432/el_documento_test"
flywayUser in Test := "postgres"
flywayPassword in Test := ""
flywayBaselineOnMigrate := true

envFileName in ThisBuild := "dotenv"

fork in run := true
cancelable in Global := true