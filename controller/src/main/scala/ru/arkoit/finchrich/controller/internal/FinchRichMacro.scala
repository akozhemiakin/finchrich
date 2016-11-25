package ru.arkoit.finchrich.controller.internal

import io.finch.Endpoint
import ru.arkoit.finchrich.controller.Controller
import scala.reflect.macros.whitebox
import macrocompat.bundle

@bundle
private[finchrich] class FinchRichMacro(val c: whitebox.Context) {
  import c.universe._

  def materialize[T <: Controller : c.WeakTypeTag, R : c.WeakTypeTag]: Tree = {
    def symbolResultType(s: Symbol): Type = s match {
      case x if x.isMethod => x.asMethod.returnType
      case x => x.typeSignature
    }

    def filterApplicableTerms(t: Type) =
      t.members
        .filter(x => x.isTerm && x.isPublic && !x.isSynthetic)
        .map(_.asTerm)
        .filter {
          case x if x.isMethod =>
            val ms = x.asMethod
            !ms.isConstructor && (ms.returnType <:< c.weakTypeOf[Controller] | ms.returnType <:< c.weakTypeOf[Endpoint[_]])
          case x if !x.isMethod => x.typeSignature <:< c.weakTypeOf[Controller] | x.typeSignature <:< c.weakTypeOf[Endpoint[_]]
          case _ => false
        }

    def extract(t: Type, context: Tree): List[(Tree, Type)] = {
      filterApplicableTerms(t).toList.flatMap{
        case x if symbolResultType(x) <:< c.weakTypeOf[Controller] => extract(symbolResultType(x), q"$context.${x.name.toTermName}")
        case x => List((q"$context.${x.name.toTermName}", symbolResultType(x).typeArgs.head))
      }
    }

    val (exSyms, exTypes) = extract(c.weakTypeOf[T], q"a").unzip

    if (exSyms.isEmpty)
      c.abort(c.enclosingPosition, "Controller passed to the controllerToEndpoint function does not contain neither endpoints nor other non-empty controllers.")

    val result = exSyms.foldLeft(q"": Tree)((a, b) => a match {
      case q"" => q"$b"
      case _ => q"$a.:+:($b)"
    })

    val resultType = if (exTypes.length == 1) q"${exTypes.head}" else
      exTypes.reverse.foldRight(tq"": Tree)((a, b) => b match {
        case tq"" => tq":+:[$a, CNil]"
        case _ => tq":+:[$a, $b]"
      })

    q"""
      import io.finch.Endpoint
      import shapeless._
      import ru.arkoit.finchrich.controller.EndpointExtractor

      new EndpointExtractor[${c.weakTypeOf[T]}] {
        type R = $resultType

        def apply(a: ${c.weakTypeOf[T]}): Endpoint[R] = $result
      }
      """
  }
}
