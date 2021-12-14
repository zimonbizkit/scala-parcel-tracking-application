package com.eduardsimon.core.module.application

import com.eduardsimon.core.module.domain.{Parcel}

final case class RegisterShipmentCommand(reference: String, parcels: List[Parcel])
final case class PushTrackingCommand(status: String, parcelNumber: Option[Int], weight: Option[Int], reference: String)