package com.eduardsimon.core.module.application

import com.eduardsimon.core.module.domain.{FindMatchForShipment, NotNeeded, Parcel, ReferenceId, Shipment, ShipmentRepository, TrackingRepository}
import com.eduardsimon.sharedkernel.module.domain.SuccessfulResponse
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{BeforeAndAfter, Ignore}
import org.scalatest.matchers.should.Matchers

class RegisterShipmentUseCaseUnitTest extends AnyFunSuite with Matchers with MockFactory {
  val VALID_REFERENCE_ID = "ABCD123456"
  val INVALID_REFERENCE_ID = "ABCD12345A"

  val mockedShipmentRepository: ShipmentRepository = mock[ShipmentRepository]
  val mockedTrackingMatcher: FindMatchForShipment = mock[FindMatchForShipment]

  ShipmentDependencies.shipmentRepository = mockedShipmentRepository
  ShipmentDependencies.shipmentsMatcher = mockedTrackingMatcher

  test("It should persist shipments properly") {
    val fakeShipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(Parcel(1, 2, 3, 4)))

    (mockedTrackingMatcher.find _).expects(fakeShipment).returns(None)
    (mockedShipmentRepository.save _).expects(fakeShipment)
    (mockedShipmentRepository.all _).expects().returns(List(fakeShipment))
    val result = RegisterShipmentUseCase(RegisterShipmentCommand(
      fakeShipment.reference.value,
      fakeShipment.parcels
    ))

    assert(result.isLeft)

    result.left.getOrElse(null) shouldBe a[SuccessfulResponse[String]]
  }


  test("It should persist shipments properly and fire a domain event if a matching tracking exists") {
    val fakeShipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(Parcel(1, 2, 3, 4)))

    (mockedTrackingMatcher.find _).expects(fakeShipment).returns(Some(NotNeeded))
    (mockedShipmentRepository.save _).expects(fakeShipment)
    (mockedShipmentRepository.all _).expects().returns(List(fakeShipment))
    val result = RegisterShipmentUseCase(RegisterShipmentCommand(
      fakeShipment.reference.value,
      fakeShipment.parcels
    ))

    assert(result.isLeft)

    result.left.getOrElse(null) shouldBe a[SuccessfulResponse[String]]
  }
  test("Shipment persistence should fail if reference is not properly formatted"){
    val result = RegisterShipmentUseCase(RegisterShipmentCommand(
      INVALID_REFERENCE_ID,
      List(Parcel(1,2,3,4))
    )
    )
    (mockedShipmentRepository.all _).expects().never()
    assert(result.isRight)

  }
}
