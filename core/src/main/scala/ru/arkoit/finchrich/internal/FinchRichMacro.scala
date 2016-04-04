package ru.arkoit.finchrich.internal

import io.finch.Endpoint
import ru.arkoit.finchrich.Controller
import scala.reflect.macros.whitebox

private[finchrich] object FinchRichMacro {
  def controllerToEndpoint[T <: Controller : c.WeakTypeTag](c: whitebox.Context)(cnt: c.Expr[T]): c.Expr[Any] = {
    import c.universe._

    def filterApplicableTerms(t: c.universe.Type) =
      t.members
        .filter(x => x.isTerm && x.isPublic)
        .map(_.asTerm)
        .filter{
          case x if x.isMethod =>
            val ms = x.asMethod
            !ms.isConstructor && (ms.returnType <:< c.weakTypeOf[Controller] | ms.returnType <:< c.weakTypeOf[Endpoint[_]])
          case x if !x.isMethod => x.typeSignature <:< c.weakTypeOf[Controller] | x.typeSignature <:< c.weakTypeOf[Endpoint[_]]
          case _ => false
        }

    def extract(t: c.universe.Type, context: Tree): List[Tree] = {
      filterApplicableTerms(t).toList.flatMap{
        case x if x.typeSignature.resultType <:< c.weakTypeOf[Controller] => extract(x.typeSignature.resultType, q"$context.${x.name.toTermName}")
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

    val code = q"""
        import io.finch._
        import shapeless._
        import ru.arkoit.finchrich._

        $v
      """

    c.Expr(code)
  }
}
