package ru.arkoit.finchrich

import ru.arkoit.finchrich.controller.internal.FinchRichMacro
import scala.language.experimental.macros

package object controller {
  def controllerToEndpoint[T <: Controller](cnt: T): Any = macro FinchRichMacro.controllerToEndpoint[T]
}
