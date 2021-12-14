package com.eduardsimon.core.module.application

import com.eduardsimon.core.module.domain.{Delivered, ReferenceId, Tracking}
import com.eduardsimon.sharedkernel.module.domain.{FailureResponse, SuccessfulResponse}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PushTrackingUseCaseIntegrationTest extends AnyFunSuite with Matchers with BeforeAndAfter {
  val VALID_REFERENCE_ID = "ABCD123456"
  val INVALID_REFERENCE_ID = "ABCD12345A"
  val INVALID_TRACKING_STATUS = "RETAINED_IN_CUSTOMS"
  val fakeTracking: Tracking = Tracking(Delivered, Some(12), Some(1), ReferenceId(VALID_REFERENCE_ID))

  after {
    TrackingDependencies.trackingRepository.flush()
  }


  test("It should persist trackings properly") {

    val result = PushTrackingUseCase(PushTrackingCommand(
      fakeTracking.trackingStatus.toString,
      fakeTracking.parcelNumber,
      fakeTracking.weight,
      fakeTracking.reference.value
    )
    )

    assert(result.isLeft)
    result.left.getOrElse(null) shouldBe a[SuccessfulResponse[String]]
    result.left.getOrElse(null).response should equal(List(fakeTracking).toString)
    TrackingDependencies.trackingRepository.all() should equal(List(fakeTracking))
  }
  test("Tracking persistence should fail too if reference id is not properly formatted"){

    val result = PushTrackingUseCase(PushTrackingCommand(
      fakeTracking.trackingStatus.toString,
      fakeTracking.parcelNumber,
      fakeTracking.weight,
      INVALID_REFERENCE_ID
    )
    )

    assert(result.isRight)
    TrackingDependencies.trackingRepository.all() should equal(List())
  }
  test("Tracking persistence should fail if the status is not a valid one") {
    val result = PushTrackingUseCase(PushTrackingCommand(
      INVALID_TRACKING_STATUS,
      fakeTracking.parcelNumber,
      fakeTracking.weight,
      INVALID_REFERENCE_ID
    )
    )

    assert(result.isRight)

    result.right.getOrElse(null) shouldBe a[FailureResponse[String]]
    TrackingDependencies.trackingRepository.all() should equal(List())
  }
}
