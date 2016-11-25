package ru.arkoit.finchrich

import io.finch.Endpoint

package object controller extends EndpointExtractor.ToOps {
  @deprecated("Use toEndpoint on the controller instance instead", "0.2.0")
  def controllerToEndpoint[T <: Controller](cnt: T)
    (implicit cte: EndpointExtractor[T]): Endpoint[cte.R] =
    cte(cnt)
}
