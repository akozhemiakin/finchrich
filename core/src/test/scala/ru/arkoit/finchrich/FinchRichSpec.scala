package ru.arkoit.finchrich

import io.finch._
import org.scalatest.{FlatSpec, Matchers}
import scala.reflect.runtime.universe._
import shapeless._

class FinchRichSpec extends FlatSpec with Matchers {
  behavior of "FinchRich"

  it should "be able to extract endpoints coproduct from the controller" in {
    object A extends Controller {
      val ep1 = get("hello") { Ok("foo") }
      val ep2 = get("bar") { Ok() }
    }

    object B extends Controller {
      val ep1 = get("foos") { Ok("foo") }
      val ep2 = get("bars") { Ok() }
    }

    object C extends Controller {
      val c1 = A
      val c2 = B
    }

    val b = controllerToEndpoint(C)

    def checkFinalType[T: TypeTag](b: T): Boolean = {
      typeOf[T] match {
        case x if x =:= typeOf[Endpoint[Unit :+: String :+: Unit :+: String :+: CNil]] => true
        case _ => false
      }
    }

    assert(checkFinalType(b))

    // Finally, check that the resulting endpoint may be smoothly converted to the finagle service
    b.toService
  }
}
