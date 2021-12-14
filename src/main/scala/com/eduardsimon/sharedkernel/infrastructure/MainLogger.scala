package com.eduardsimon.sharedkernel.infrastructure

import akka.event.LoggingAdapter

object MainLogger {
  var logger: Option[LoggingAdapter] = None
  def info(message: String): Unit = {
    logger match {
      case Some(logger) => logger.info(message)
      case _ => println(message)
    }
  }
}
