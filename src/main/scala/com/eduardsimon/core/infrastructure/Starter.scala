package com.eduardsimon.core.infrastructure

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import com.eduardsimon.core.infrastructure.http.Routing
import com.eduardsimon.sharedkernel.infrastructure.MainLogger
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

object Starter extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = system.dispatcher

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  MainLogger.logger = Some(logger)

  Http()
    .newServerAt(config.getString("http.interface"), config.getInt("http.port"))
    .bindFlow(Routing.routes)
}
