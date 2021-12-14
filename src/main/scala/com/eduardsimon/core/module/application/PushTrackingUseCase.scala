package com.eduardsimon.core.module.application

import cats.data.Reader
import com.eduardsimon.core.infrastructure.repository.InMemoryTrackingRepository
import com.eduardsimon.core.module.domain.{Delivered, ReferenceId, Tracking, TrackingRepository, WaitingInHub}
import com.eduardsimon.sharedkernel.module.domain.{DomainException, FailureResponse, SuccessfulResponse}

object TrackingDependencies {
  val trackingRepository: TrackingRepository = new InMemoryTrackingRepository
}

object PushTrackingUseCase {
  def apply(command: PushTrackingCommand): Either[SuccessfulResponse[String], FailureResponse[_]] = {

    try {
      val useCase = for {
        _ <- saveTracking(
          Tracking(
            command.status match {
              case "DELIVERED" => Delivered
              case "WAITING_IN_HUB" => WaitingInHub
              case _ => throw DomainException("Tracking status is invalid")
            },
            command.parcelNumber,
            command.weight,
            ReferenceId(command.reference)
          )
        )
        trackings <- printExistingTrackings()
      } yield trackings

      Left(SuccessfulResponse(useCase.run(TrackingDependencies.trackingRepository)))
    } catch {
      case e: DomainException => Right(FailureResponse(e.message))
    }
  }

  private def saveTracking(tracking: Tracking): Reader[TrackingRepository, Unit] = Reader { repository =>
    repository.save(tracking)
  }

  private def printExistingTrackings(): Reader[TrackingRepository, String] = Reader { repository =>
    repository.all().toString()
  }
}
