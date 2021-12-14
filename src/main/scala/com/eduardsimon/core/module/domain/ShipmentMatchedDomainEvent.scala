package com.eduardsimon.core.module.domain

import com.eduardsimon.sharedkernel.module.domain.{DomainEvent, EventListener}

trait MatchStatus

object ConcilliationRequest extends MatchStatus {
  override def toString: String = "CONCILLIATION_REQUEST"
}

object NotNeeded extends MatchStatus {
  override def toString: String = "NOT_NEEDED"
}

object Incomplete extends MatchStatus {
  override def toString: String = "INCOMPLETE"
}

object NotFound extends MatchStatus {
  override def toString: String = "NOT_FOUND"
}

case class ShipmentMatchedDomainEvent(referenceId: ReferenceId, matchStatus: MatchStatus)
  extends DomainEvent {
  override val eventName = "shipment_matched"
  val listeners : List[EventListener] = List(LogMatchingToStandardOutputOnShipmentMatchedListener)

  listeners.foreach(_.listen(this))
}
