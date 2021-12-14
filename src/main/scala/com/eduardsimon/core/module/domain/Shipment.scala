package com.eduardsimon.core.module.domain

import com.eduardsimon.sharedkernel.module.domain.DomainException

case class Parcel(weight: Int, width: Int, height: Int, length: Int)

case class ReferenceId(value: String)

object ReferenceId {
  val Pattern = """^([A-Z]{4}[0-9]{6})""".r

  def apply(reference: String): ReferenceId = {
    if (!Pattern.matches(reference)){
      throw DomainException(s"Reference should have ${Pattern.regex} format")
    }
    new ReferenceId(reference)
  }
}

case class Shipment(reference: ReferenceId, parcels: List[Parcel]) {
  val totalWeight: Int = parcels.map(_.weight).sum
  val parcelNumber: Int = parcels.size
}
