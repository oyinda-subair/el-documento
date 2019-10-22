package org.el.documento.route

import akka.Done
import akka.http.scaladsl.model._
import org.el.documento.DocumentoRouteTestkit
import org.el.documento.messages.{CreateRoleRequest, CreateUserRequest, LoginByEmail}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.el.documento.Util._
import org.el.documento.model.{UserClaim, UserToken}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class UserRouteSpec extends WordSpec with Matchers with DocumentoRouteTestkit with BeforeAndAfterAll with ScalaFutures {

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val role = CreateRoleRequest("public", Some("public access only"))
    val inserted = for {
      _ <- controller.createRole(role)
    } yield Done

    Await.result(inserted, 50 seconds)
  }

  override protected def afterAll(): Unit = {
    super.afterAll()

    val deleted = for {
      _ <- db.UserRepo.deleteAll()
      _ <- db.RoleRepo.deleteAll()
    } yield Done

    Await.result(deleted, 10 seconds)
  }

  "El Documento user Route" when {
    "User Endpoints" should {
      "create user " in {
        val userEntity = CreateUserRequest("test user", "usertest", s"user-$string10", s"$string10")

        Post(s"/$api/$version/users").withEntity(toEntity(userEntity)) ~> route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }

      "Login use" in {
        val userEntity = CreateUserRequest("test user login", "userlogin", s"user-login-$string10", s"$string10")
        Post(s"/$api/$version/users").withEntity(toEntity(userEntity)) ~> route ~> check {
          status shouldEqual StatusCodes.Created

          val login = LoginByEmail(userEntity.email, userEntity.password)
          Post(s"/$api/$version/login").withEntity(toEntity(login)) ~> route ~> check {
            status shouldEqual StatusCodes.OK
          }
        }
      }
    }

    "Role Endpoint" should {
      "create role" in {
        val roleRequest = CreateRoleRequest("sample role", Some("sample role"))

        Post(s"/$api/$version/roles").withEntity(toEntity(roleRequest)) ~> route ~> check {
          status shouldEqual StatusCodes.Unauthorized
        }
      }
    }
  }
}
