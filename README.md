[![Build Status](https://travis-ci.org/akozhemiakin/finchrich.svg?branch=master)](https://travis-ci.org/akozhemiakin/finchrich)

This tiny project is a sort of experimental toolset which aims to allow 
developers use [Finch][finch] more efficiently and with less boilerplate.

For now it offers only one thing to simplify your life with Finch: 
controllers implemented without any kind of runtime reflection.


Installation
------------
For now there is only one module available. You can use the following _sbt_ snippet to install it:

* for the _stable_ release:

```scala
libraryDependencies ++= Seq(
  "ru.arkoit" %% "finchrich-controller" % "0.1.0"
)
```

* for the `SNAPSHOT` version:

```scala
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "ru.arkoit" %% "finchrich-controller" % "0.1.1-SHANPSHOT"
)
```

Usage
-----
As mentioned above, for now the project consists of the only module: finchrich-controller.

First of all it allows you to define Finch endpoints within the 
controllers and extract them later in the form of endpoints coproducts:

```scala
import io.finch._
import ru.arkoit.finchrich.controller._

object MyAwesomeController extends Controller {
  val healthcheck = get("healthcheck") { Ok() }
  val greeter = get("greet" / param("name")) { name: String => Ok(s"Hello, $name!") }
}

// Get the coproduct of all endpoints from the MyAwesomeControlller
val ep = controllerToEndpoint(MyAwesomeController)
```

Also it allows you to nest controllers like this:

```scala
import io.finch._
import ru.arkoit.finchrich.controller._

object MyAwesomeController extends Controller {
  val healthcheck = get("healthcheck") { Ok() }
  val greeter = get("greet" / param("name")) {name: String => Ok(s"Hello, $name!") }
}

object AnotherAwesomeController extends Controller {
  val joke = get("joke") { Ok("""Chuck Norris died 20 years ago, 
    Death just hasn't built up the courage to tell him yet.""") }
}

object MainController extends Controller {
  val c1 = MyAwesomeController
  val c2 = AnotherAwesomeController
}

// Get the coproduct of all endpoints from the MainController and nested controllers
val ep = controllerToEndpoint(MainController)
```

All of this stuff does not use any kind of runtime reflection and is
actually implemented as a whitebox macros. It expands in something that
you usually write by hand, like this:

```scala
val ep = MainController.c1.healthcheck :+: MainController.c1.greeter :+: MainController.c2.joke
```

[Finch]: https://github.com/finagle/finch
