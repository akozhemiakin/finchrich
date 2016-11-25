package ru.arkoit.finchrich.controller

import io.finch._
import org.scalatest.{FlatSpec, Inside, Matchers}

class EndpointExtractorSpec extends FlatSpec with Matchers with Fixtures {
  behavior of "EndpointExtractor"

  it should "transform controller to an endpoint" in {
    val cnt = new ComplexController()
    val ep = cnt.toEndpoint
    checkEndpointType(ep)
    ep.toServiceAs[Text.Plain]
  }
}
