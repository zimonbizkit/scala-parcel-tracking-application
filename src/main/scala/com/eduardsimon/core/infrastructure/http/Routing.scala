package com.eduardsimon.core.infrastructure.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.eduardsimon.core.module.application.{PushTrackingCommand, PushTrackingUseCase, RegisterShipmentCommand, RegisterShipmentUseCase}
import com.eduardsimon.core.module.domain._
import spray.json._

trait JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val parcelFormat = jsonFormat4(Parcel)
  implicit val registerShipmentCommandFormat = jsonFormat2(RegisterShipmentCommand)
  implicit val pushTrackingCommandFormat = jsonFormat4(PushTrackingCommand)
}

object Routing extends Directives with JsonProtocol {
  val rootResource = "api"

  val routes: Route = {
    def registerUseCase = {
      path("register") {
        post {
          entity(as[RegisterShipmentCommand]) { registerShipmentCommand =>
            val response = RegisterShipmentUseCase(registerShipmentCommand)
            response match {
              case Left(v) => complete(StatusCodes.Created)
              case _ => complete(StatusCodes.BadRequest)
            }
          }
        }
      }
    }

    def pushUseCase = {
      path("push") {
        put {
          entity(as[PushTrackingCommand]) { pushTrackingCommand =>
            val response = PushTrackingUseCase(pushTrackingCommand)
            response match {
              case Left(v) => complete(StatusCodes.Created)
              case _ => complete(StatusCodes.BadRequest)
            }
          }
        }
      }
    }

    logRequestResult("challenge") {
      pathPrefix(rootResource) {
        concat(registerUseCase, pushUseCase)
      }
    }
  }
}
