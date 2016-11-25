package ru.arkoit.finchrich.controller

import ru.arkoit.finchrich.controller.internal.FinchRichMacro
import scala.language.experimental.macros
import io.finch.Endpoint

trait EndpointExtractor[A <: Controller] {
  type R

  def apply(c: A): Endpoint[R]
}

object EndpointExtractor {
  type Aux[C <: Controller, RR] = EndpointExtractor[C] { type R = RR }

  def apply[A <: Controller](implicit t: EndpointExtractor[A]): Aux[A, t.R] = t

  implicit def materialize[A <: Controller, R]: Aux[A, R] =
    macro FinchRichMacro.materialize[A, R]

  trait Ops[A <: Controller, R] {
    def toEndpoint: Endpoint[R]
  }

  trait ToOps {
    implicit def toEndpointExtractorOps[A <: Controller]
      (c: A)(implicit cte: EndpointExtractor[A]): Ops[A, cte.R] =
      new Ops[A, cte.R] {
        override def toEndpoint: Endpoint[cte.R] = cte(c)
      }
  }
}
