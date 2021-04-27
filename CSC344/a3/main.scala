import scala.io.StdIn._

abstract class S {
  def eval(env: Main.Environment): Boolean
}

case class A(left: Option[Const], right: Option[E]) extends S {
  def eval(env: Main.Environment): Boolean = {
    val l: Boolean = left match {
      case Some(x) => x.eval(env)
      case None    => false
    }
    val r: Boolean = right match {
      case Some(x) => x.eval(env)
      case None    => false
    }
    l || r
  }
}

case class E(left: T, right: Option[E2]) extends S {
  def eval(env: Main.Environment): Boolean = {
    val l: Boolean = left.eval(env)
    right match {
      case Some(x) => (l || x.eval(env))
      case None    =>  l
    }
  }
}
// Or Case
case class E2(right: Option[E3]) extends S {
  def eval(env: Main.Environment): Boolean = {
    right match {
      case Some(x) => x.eval(env)
    }
  }
}
// Or Case Extended
case class E3(left: T, right: Option[E2]) extends S {
  def eval(env: Main.Environment): Boolean = {
    val l: Boolean = left.eval(env)
    right match {
      case Some(x) => (l || x.eval(env))
      case None    =>  l
    }
  }
}
// Continuous Word
case class T(left: F, right: Option[T2]) extends S {
  def eval(env: Main.Environment): Boolean = {
    val l: Boolean = left.eval(env)
    right match {
      case Some(x) => ( l && x.eval(env) )
      case None    =>   l
    }
  }
}

case class T2(left: F, right: Option[T2]) extends S {
  def eval(env: Main.Environment): Boolean = {
    val l: Boolean = left.eval(env)
    right match {
      case Some(x) => ( l && x.eval(env) )
      case None    =>   l
    }
  }
}
case class F(left: A, right: Option[F2]) extends S {
  def eval(env: Main.Environment): Boolean = {
    val l: Boolean = left.eval(env)
    right match {
      case Some(x) =>   l && x.eval(env)
      case None    =>   l
    }
//    right match {
//      case Some(x) => x.eval(env)
//      case None    => true
//    }
  }
}
case class F2(right: Option[F2]) extends S {
  def eval(env: Main.Environment): Boolean = {
    true
  }
}
case class Const(v: Char) extends S {
  def eval(env: Main.Environment): Boolean = {
    v match {
      case v =>
        if (v == '.' || env.Str(env.index) == v) {
          env.index += 1
          true
        }
        else false
    }
  }
}

class RecursiveDecentParser(input: String) {
  var index = 0

  def parseE(): E = E(parseT(), parseE2())
  def parseE2(): Option[E2] = {
    if (index < input.length && input.charAt(index) == ')') {
      index += 1
      None
    }
    else {
      if (index < input.length && input.charAt(index) == '|') {
        index += 1
        Some(E2(parseE3()))
      }
      else None
    }
  }
  def parseA(): A = {
    if (index < input.length && input.charAt(index) == '(') {
      index += 1
      A(None, Some(parseE()))
    }
    else {
      index += 1
      A(Some(Const(input.charAt(index - 1))) , None)
    }
  }
  def parseE3(): Option[E3] = Some(E3(parseT(), parseE2()))
  def parseT(): T = T(parseF(), parseT2())
  def parseT2(): Option[T2] = {
    if (index < input.length && input.charAt(index) != ')' && input.charAt(index) != '|' && input.charAt(index) != '?') {
      Some(T2(parseF(), parseT2()))
    }
    else None
  }
  def parseF(): F = F(parseA(), parseF2())
  def parseF2(): Option[F2] = {
    if (index < input.length && input.charAt(index) == '?') {
      index += 1
      Some(F2(parseF2()))
    }
    else None
  }
}

object main {
  class Environment(var Str: String, var index: Int)

  def main(args: Array[String]): Unit = {
    print("pattern? ")
    val pattern: String = readLine()
    val parser: RecursiveDecentParser = new RecursiveDecentParser(pattern)
    val parsedExp: S = parser.parseE()
    var string: String = null
    while (true) {
      print("string? ")
      var string: String = readLine()
      val env: Environment = new Environment(string, 0)
      println(parsedExp.eval(env))
    }

  }
}
