package com.eduardsimon.core.module.domain

import java.util.logging.Logger

import com.eduardsimon.sharedkernel.infrastructure.MainLogger
import com.eduardsimon.sharedkernel.module.domain.{DomainEvent, EventListener}

object LogMatchingToStandardOutputOnShipmentMatchedListener extends EventListener{
  override def listen(domainEvent: DomainEvent): Unit = MainLogger.info(domainEvent.toString)
}
