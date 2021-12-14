package com.eduardsimon.core.module.domain

import com.eduardsimon.sharedkernel.module.domain.DomainException
import org.scalatest.Ignore
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReferenceUnitTest extends AnyFlatSpec with Matchers {
  val VALID_REFERENCE_NUMBER = "ABCD123456"
  val INVALID_REFERENCE_NUMBER = "AABCD12345"
  "A Reference" should "be built successfully if provided with proper format " in {
    assert(ReferenceId(VALID_REFERENCE_NUMBER).value == VALID_REFERENCE_NUMBER)
  }

  "A Reference" should "throw an error upon building if provided with incorrect format" in {
    assertThrows[DomainException]{
      ReferenceId(INVALID_REFERENCE_NUMBER)
    }
  }
}
