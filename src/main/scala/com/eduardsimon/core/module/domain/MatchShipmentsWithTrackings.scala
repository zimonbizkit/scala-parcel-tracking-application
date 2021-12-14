package com.eduardsimon.core.module.domain

import cats.data.{NonEmptyList, Reader}
import com.eduardsimon.core.infrastructure.repository.InMemoryTrackingRepository
import com.eduardsimon.sharedkernel.module.domain.DomainEvent

object MatchShipmentWithTrackingsDependencies {
  var trackingRepository: TrackingRepository = new InMemoryTrackingRepository
}

class FindMatchForShipment {
  def find(shipment: Shipment): Option[MatchStatus] = {

    val trackingStatusCombinations = getTrackings()
      .run(MatchShipmentWithTrackingsDependencies.trackingRepository)
      .map {
        case Tracking(Delivered, Some(shipment.parcelNumber), Some(x), ReferenceId(shipment.reference.value))
          if x < shipment.totalWeight => Some(ConcilliationRequest)
        case Tracking(Delivered, Some(shipment.parcelNumber), Some(x), ReferenceId(shipment.reference.value))
          if x >= shipment.totalWeight => Some(NotNeeded)
        case Tracking(WaitingInHub, Some(_), Some(_), ReferenceId(shipment.reference.value)) => Some(Incomplete)
        case Tracking(WaitingInHub, None, _, ReferenceId(shipment.reference.value)) => Some(Incomplete)
        case Tracking(WaitingInHub, _, None, ReferenceId(shipment.reference.value)) => Some(Incomplete)
        case Tracking(_,_,_,x) if x.value != shipment.reference.value => Some(NotFound)
        case _ => None
      }
    if (trackingStatusCombinations.isEmpty) None else trackingStatusCombinations.take(1).head
  }

  private def getTrackings(): Reader[TrackingRepository, List[Tracking]] = Reader { repository =>
    repository.all()
  }
}
