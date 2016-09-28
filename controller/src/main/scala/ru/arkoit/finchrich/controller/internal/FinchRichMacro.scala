package ru.arkoit.finchrich.controller.internal

import io.finch.Endpoint
import ru.arkoit.finchrich.controller.Controller
import scala.reflect.macros.whitebox
import macrocompat.bundle

@bundle
private[finchrich] class FinchRichMacro(val c: whitebox.Context) {
  import c.universe._

  def controllerToEndpoint[T <: Controller : c.WeakTypeTag](cnt: c.Expr[T]): Tree = {

    def symbolResultType(s: Symbol): Type = s match {
      case x if x.isMethod => x.asMethod.returnType
      case x => x.typeSignature
    }

    def filterApplicableTerms(t: Type) =
      t.members
        .filter(x => x.isTerm && x.isPublic && !x.isSynthetic)
        .map(_.asTerm)
        .filter{
          case x if x.isMethod =>
            val ms = x.asMethod
            !ms.isConstructor && (ms.returnType <:< c.weakTypeOf[Controller] | ms.returnType <:< c.weakTypeOf[Endpoint[_]])
          case x if !x.isMethod => x.typeSignature <:< c.weakTypeOf[Controller] | x.typeSignature <:< c.weakTypeOf[Endpoint[_]]
          case _ => false
        }

    def extract(t: Type, context: Tree): List[Tree] = {
      filterApplicableTerms(t).toList.flatMap{
        case x if symbolResultType(x) <:< c.weakTypeOf[Controller] => extract(symbolResultType(x), q"$context.${x.name.toTermName}")
        case x => List(q"$context.${x.name.toTermName}")
      }
    }

    val v = extract(c.weakTypeOf[T], q"$cnt").foldLeft(q"": Tree)((a, b) => a match {
      case q"" => q"$b"
      case x => q"$x :+: $b"
    }) match {
      case q"" => c.abort(c.enclosingPosition, "Controller passed to the controllerToEndpoint function does not contain neither endpoints nor other non-empty controllers.")
      case x => x
    }

    q"""
      import io.finch._
      import shapeless._
      import ru.arkoit.finchrich._

      $v
      """
  }
}
