package ru.arkoit

import io.finch.Endpoint
import ru.arkoit.finchrich.internal.FinchRichMacro
import scala.language.experimental.macros

package object finchrich {
  def controllerToEndpoint[T <: Controller](cnt: T): Any = macro FinchRichMacro.controllerToEndpoint[T]
}
