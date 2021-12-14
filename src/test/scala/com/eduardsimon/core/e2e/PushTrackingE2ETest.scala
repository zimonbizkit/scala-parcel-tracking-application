package com.eduardsimon.core.e2e

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.eduardsimon.core.infrastructure.http.Routing
import com.eduardsimon.core.module.application.PushTrackingCommand
import org.scalatest.{DoNotDiscover, GivenWhenThen}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import spray.json.DefaultJsonProtocol


class RegisterTrackingE2ETest
  extends AnyFunSpec
    with GivenWhenThen
    with Matchers
    with SprayJsonSupport with DefaultJsonProtocol
    with ScalatestRouteTest {


  implicit val pushTrackingCommandFormat = jsonFormat4(PushTrackingCommand)

  val INVALID_REFERENCE_ID = "AABCD123456"
  val VALID_REFERENCE_ID = "ABCD123456"
  val routesToTest = Routing.routes
  val TRACKING_ENDPOINT = "/api/push"

  describe(
    "In order to fulfill the Register requirements\n" +
      "As a candidate\n" +
      "I should provide a /api/register endpoint with desired payloads"
  ) {

    it("should fail with a specific response code if no payload is specified") {

      Given("The API is ready")

      When("There is a PUT to /api/register without body")
      val result = Put(TRACKING_ENDPOINT) ~!> routesToTest

      Then("The response code should be 400")
      result ~> check {
        status shouldBe BadRequest
      }

      Then("The content type should be text/plain")
      result ~> check {
        contentType shouldBe ContentTypes.`text/plain(UTF-8)`
      }
    }

    it("should fail with a specific response code if invalid reference is specified") {

      Given("The API is ready")

      When("There is a POST to /api/register without body")
      val command = PushTrackingCommand(
        "WAITING_IN_HUB",
        Some(2),
        Some(3),
        INVALID_REFERENCE_ID
      )
      val result = Put(TRACKING_ENDPOINT, command) ~!> routesToTest

      Then("The response code should be 400")
      result ~> check {
        status shouldBe BadRequest
      }

      Then("The content type should be text/plain")
      result ~> check {
        contentType shouldBe ContentTypes.`text/plain(UTF-8)`
      }
    }


    it("should push a tracking properly with proper payload") {
      Given("The API is ready")
      When("There is a POST to /api/register with specific payload")
      val command = PushTrackingCommand(
        "WAITING_IN_HUB",
        Some(2),
        Some(3),
        VALID_REFERENCE_ID
      )

      val result = Put(TRACKING_ENDPOINT, command) ~!> routesToTest

      Then("The response code should be 201")
      result ~> check {
        status shouldBe Created
      }
      And("There should be a stored shipment in memory")
      //  FIXME get the value of inmemoryRepository at runtime to check the value
    }
  }
}
