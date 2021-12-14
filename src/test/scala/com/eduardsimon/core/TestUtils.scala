package com.eduardsimon.core

import java.lang.instrument.Instrumentation

import com.eduardsimon.core.infrastructure.repository.{InMemoryShipmentRepository, InMemoryTrackingRepository}
import com.eduardsimon.core.module.domain.{Shipment, Tracking}

import scala.reflect.runtime.universe._


object TestUtils {

  val SHIPMENT_REPOSITORY_QUALIFIED_NAME = "com.eduardsimon.core.infrastructure.repository.InMemoryShipmentRepository"
  val TRACKING_REPOSITORY_QUALIFIED_NAME = "com.eduardsimon.core.infrastructure.repository.InMemoryTrackingRepository"


  def getStoredShipments(): Seq[Shipment] = {
    Class.forName(SHIPMENT_REPOSITORY_QUALIFIED_NAME)
      .asInstanceOf[InMemoryShipmentRepository]
      .storedShipments.toSeq

  }

  def getStoredTrackings(): Seq[Tracking] = {
    Class.forName(TRACKING_REPOSITORY_QUALIFIED_NAME)
      .asInstanceOf[InMemoryTrackingRepository]
      .storedTrackings
      .toSeq

  }
}
