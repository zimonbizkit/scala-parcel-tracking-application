package com.eduardsimon.core.module.application

import cats.data.Reader
import com.eduardsimon.core.infrastructure.repository.InMemoryShipmentRepository
import com.eduardsimon.core.module.domain.{FindMatchForShipment, MatchStatus, Parcel, ReferenceId, Shipment, ShipmentMatchedDomainEvent, ShipmentRepository}
import com.eduardsimon.sharedkernel.module.domain.{DomainException, FailureResponse, SuccessfulResponse}

object ShipmentDependencies {
  var shipmentRepository: ShipmentRepository = new InMemoryShipmentRepository
  var shipmentsMatcher:  FindMatchForShipment = new FindMatchForShipment
}

object RegisterShipmentUseCase {
  def apply(command: RegisterShipmentCommand): Either[SuccessfulResponse[String], FailureResponse[_]] = {

    try {
      val shipment = Shipment(
        ReferenceId(command.reference),
        command.parcels.map(x => Parcel(x.weight, x.width, x.height, x.length))
      )

      val shipmentSaver = saveShipment(shipment)

      shipmentSaver.run(ShipmentDependencies.shipmentRepository)

      val status = findMatchingStatus(shipment)
      .run(ShipmentDependencies.shipmentsMatcher)

      status match {
        case Some(x) => ShipmentMatchedDomainEvent(shipment.reference,x)
        case None => "none found"
      }

      val existingShipments = getExistingShipments.run(ShipmentDependencies.shipmentRepository)
      Left(SuccessfulResponse(existingShipments.toString()))
    } catch {
      case e: DomainException => Right(FailureResponse(e.message))
    }
  }

  private def saveShipment(shipment: Shipment): Reader[ShipmentRepository, Unit] = Reader { repository =>
    repository.save(shipment)
  }

  private def getExistingShipments: Reader[ShipmentRepository, List[Shipment]] = Reader { repository =>
    repository.all()
  }

  private def findMatchingStatus(shipment: Shipment): Reader[FindMatchForShipment, Option[MatchStatus]] = Reader { service =>
    service.find(shipment)
  }
}
