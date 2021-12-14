package com.eduardsimon.core.infrastructure.repository

import com.eduardsimon.core.module.domain.{Shipment, ShipmentRepository, Tracking, TrackingRepository}

import scala.collection.mutable.ListBuffer

class InMemoryShipmentRepository
  extends ShipmentRepository {
  var storedShipments: ListBuffer[Shipment] = ListBuffer.empty

  override def save(shipment: Shipment): Unit = storedShipments.addOne(shipment)

  override def all():List[Shipment] = storedShipments.toList

  override def flush(): Unit = {
    storedShipments = ListBuffer.empty
  }
}

class InMemoryTrackingRepository
  extends TrackingRepository {
  var storedTrackings: ListBuffer[Tracking] = ListBuffer.empty

  override def save(tracking: Tracking): Unit =storedTrackings.addOne(tracking)

  override def all():List[Tracking] = storedTrackings.toList

  override def flush(): Unit = {
    storedTrackings = ListBuffer.empty
  }
}
