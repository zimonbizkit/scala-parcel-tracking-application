package com.eduardsimon.core.module.domain

sealed trait TrackingStatus
 object Delivered extends TrackingStatus {
   override def toString: String = "DELIVERED"
 }
object WaitingInHub extends TrackingStatus {
  override def toString: String = "WAITING_IN_HUB"
}

case class Tracking(trackingStatus: TrackingStatus, parcelNumber: Option[Int], weight: Option[Int], reference: ReferenceId)
