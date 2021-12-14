package com.eduardsimon.core.module.domain

trait ShipmentRepository {
  def save(shipment: Shipment): Unit

  def all(): List[Shipment]

  def flush(): Unit
}

trait TrackingRepository {
  def save(tracking: Tracking): Unit

  def all(): List[Tracking]

  def flush(): Unit
}
