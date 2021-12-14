package com.eduardsimon.core.infrastructure

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.StatusCodes.{BadRequest, MethodNotAllowed, NotFound}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.eduardsimon.core.infrastructure.http.Routing.routes
import com.typesafe.config.Config
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class ApiRestfulnessBehaviourTest extends AsyncFlatSpec with Matchers with ScalatestRouteTest {
  override def testConfigSource = "akka.loglevel = WARNING"
  def config: Config = testConfig
  val logger: NoLogging.type = NoLogging

  val INVALID_ENDPOINT = "/api/this/endpoint/does/not/exist"
  val SHIPMENT_ENDPOINT = "/api/register"
  val TRACKING_ENDPOINT = "/api/push"

  "Service" should "not handle the request if the endpoint called does not exist" in {
    Get(INVALID_ENDPOINT) ~!> routes ~> check {
      status shouldBe NotFound
    }
  }

  "Service" should "respond with a specific response code if body is empty" in {
    Post(SHIPMENT_ENDPOINT) ~!> routes ~> check {
      status shouldBe BadRequest
      contentType shouldBe ContentTypes.`text/plain(UTF-8)`
    }
    Put(TRACKING_ENDPOINT) ~!> routes ~> check {
      contentType shouldBe ContentTypes.`text/plain(UTF-8)`
    }
  }

  "Service" should "respond with a specific response code if the HTTP verb is not correct" in {
    Delete(SHIPMENT_ENDPOINT) ~!> routes ~> check {
      status shouldBe MethodNotAllowed
    }
    Post(TRACKING_ENDPOINT) ~!> routes ~> check {
      status shouldBe MethodNotAllowed
    }
  }
}
