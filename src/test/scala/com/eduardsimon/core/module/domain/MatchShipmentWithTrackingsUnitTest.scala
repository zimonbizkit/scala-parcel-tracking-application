package com.eduardsimon.core.module.domain

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MatchShipmentWithTrackingsUnitTest
  extends AnyFunSuite
    with Matchers
    with MockFactory {
  val VALID_REFERENCE_ID = "ABCD123456"
  val ANOTHER_VALID_REFERENCE_ID = "ABCD123457"

  val mockedTrackingRepository: TrackingRepository = mock[TrackingRepository]
  MatchShipmentWithTrackingsDependencies.trackingRepository = mockedTrackingRepository

  test("conditions should be met to return a ConcilliationRequest application event") {
    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(Delivered, Some(4), Some(11), ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(ConcilliationRequest)
  }

  test("A set of conditions should be met to dispatch a NotNeeded application event") {

    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(Delivered, Some(4), Some(13), ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(NotNeeded)
  }

  test("Another set of conditions should be met to dispatch a NotNeeded application event") {

    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(Delivered, Some(4), Some(12), ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(NotNeeded)
  }

  test("A set of conditions should be met to dispatch an Incomplete application event") {
    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(WaitingInHub, None, Some(12), ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(Incomplete)
  }
  test("Another set of conditions should be met to dispatch an Incomplete application event") {
    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(WaitingInHub, None, Some(12), ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(Incomplete)
  }
  test("Yet another set of conditions should be met to dispatch an Incomplete application event") {
    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(WaitingInHub, Some(13), None, ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(Incomplete)
  }

  test("Conditions should be met to dispatch a NotFound application event ") {
    (mockedTrackingRepository.all _).expects().returns(List(
      Tracking(WaitingInHub, Some(13), None, ReferenceId(VALID_REFERENCE_ID))
    )
    )

    val shipment = Shipment(ReferenceId(ANOTHER_VALID_REFERENCE_ID), List(
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
      Parcel(3, 2, 3, 4),
    ))

    val finder = new FindMatchForShipment
    val expectedStatus = finder.find(shipment)

    expectedStatus shouldBe Some(NotFound)
  }

}
