package com.eduardsimon.core.e2e

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.eduardsimon.core.infrastructure.http.Routing
import com.eduardsimon.core.module.application.RegisterShipmentCommand
import com.eduardsimon.core.module.domain.Parcel
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import spray.json.DefaultJsonProtocol

class RegisterShipmentE2ETest
  extends AnyFunSpec
    with GivenWhenThen
    with Matchers
    with ScalatestRouteTest
    with SprayJsonSupport with DefaultJsonProtocol {

  implicit val parcelFormat = jsonFormat4(Parcel)
  implicit val registerShipmentCommandFormat = jsonFormat2(RegisterShipmentCommand)

  val INVALID_REFERENCE_ID = "AABCD123456"
  val VALID_REFERENCE_ID = "ABCD123456"
  val routesToTest = Routing.routes
  val SHIPMENT_ENDPOINT = "/api/register"


  describe(
    "In order to fulfill the Register requirements\n" +
      "As a candidate\n" +
      "I should provide a /api/register endpoint with desired payloads"
  ) {

    it("should fail with a specific response code if no payload is specified") {

      Given("The API is ready")

      When("There is a POST to /api/register without body")
      val result = Post(SHIPMENT_ENDPOINT) ~!> routesToTest

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
      val command = RegisterShipmentCommand(
        INVALID_REFERENCE_ID,
        List(
          Parcel(1, 2, 3, 4),
          Parcel(1, 2, 3, 4)
        )
      )
      val result = Post(SHIPMENT_ENDPOINT,command) ~!> routesToTest

      Then("The response code should be 400")
      result ~> check {
        status shouldBe BadRequest
      }

      Then("The content type should be text/plain")
      result ~> check {
        contentType shouldBe ContentTypes.`text/plain(UTF-8)`
      }
    }


    it("should store a shipment properly with proper payload") {
      Given("The API is ready")
      When("There is a POST to /api/register with specific payload")
      val command = RegisterShipmentCommand(
        VALID_REFERENCE_ID,
        List(
          Parcel(1, 2, 3, 4),
          Parcel(1, 2, 3, 4)
        )
      )
      val result = Post(SHIPMENT_ENDPOINT, command) ~!> routesToTest

      Then("The response code should be 201")
      result ~> check {
        status shouldBe Created
      }
      And("There should be a stored shipment in memory")
      //  FIXME get the value of inmemoryRepository at runtime to check the value
      And("A specific event should have been outputted to stdout")
      //  FIXME get the value somehow from stdout to asssert the event has ben fired properly
    }
  }
}
