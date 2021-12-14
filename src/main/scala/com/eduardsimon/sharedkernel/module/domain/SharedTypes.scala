package com.eduardsimon.sharedkernel.module.domain

case class DomainException(message: String) extends Exception

sealed trait UseCaseResponse
case class SuccessfulResponse[A](response: A) extends UseCaseResponse
case class FailureResponse[A](response: A) extends UseCaseResponse

abstract class DomainEvent {
  val eventName = ""
}

trait EventListener {
  def listen(domainEvent: DomainEvent)
}