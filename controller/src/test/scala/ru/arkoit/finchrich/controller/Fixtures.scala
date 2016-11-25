package ru.arkoit.finchrich.controller

import shapeless._
import io.finch._
import scala.reflect.runtime.universe._

trait Fixtures {
  implicit def universalEncoder[A](implicit tenc: Encode.Aux[String, Text.Plain]): Encode.Aux[A, Text.Plain] =
    Encode.text[A]((a, c) => tenc(a.toString, c))

  type ComplexControllerEPType = Boolean :+: String :+: Int :+: Int :+: String :+: Unit :+: String :+: CNil

  class ControllerA extends Controller {
    val ep = get("foo") { Ok(10) }
    def ep2 = get("bar" :: string) { s: String => Ok(s)}
  }

  class ControllerB extends Controller {
    val ep = get("hehe") { Ok(true) }
    val ep2 = get("jjj") { Ok("hhh") }
  }

  case class CaseClassController (
    ep: Endpoint[Int],
    cnt: ControllerA
  ) extends Controller

  class ComplexController extends Controller {
    val cnt = new ControllerB
    val cnt2 = CaseClassController(
      ep = get("baz") { Ok(10) },
      cnt = new ControllerA
    )
    val ep = get("hojjk") { Ok() }
    val ep2 = get("mmmmm") { Ok("koko") }
  }

  def checkEndpointType[T: WeakTypeTag](b: Endpoint[T]): Boolean = {
    weakTypeOf[T] match {
      case x if x =:= weakTypeOf[ComplexControllerEPType] => true
      case _ => false
    }
  }
}
