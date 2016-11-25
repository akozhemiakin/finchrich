package ru.arkoit.finchrich.controller

import io.finch._
import org.scalatest.{FlatSpec, Matchers}

class DeprecatedFinchRichSpec extends FlatSpec with Matchers with Fixtures {
  behavior of "controllerToEndpoint (deprecated)"

  it should "transform controller to an endpoint" in {
    val cnt = new ComplexController()
    val ep = controllerToEndpoint(cnt)
    checkEndpointType(ep)
    ep.toServiceAs[Text.Plain]
  }
}
