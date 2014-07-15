package records.test

import org.scalatest._

import records.Rec

// This is for 2.10.x compatibility!
import scala.language.reflectiveCalls

class ConversionTests extends FlatSpec with Matchers {

  case class SimpleVal(a: Int)
  case class ObjectVal(myObject: AnyRef)
  case class DBRecord(name: String, age: Int, location: String)

  "A Record" should "be able to convert into a case class" in {
    val x = Rec("a" -> 1)
    val y = x.to[SimpleVal]

    y.a should be(1)
  }

  it should "be able to convert to looser case classes" in {
    val x = Rec("myObject" -> "String")
    val y = x.to[ObjectVal]

    y.myObject should be("String")
  }

  it should "be able to convert to narrower case classes" in {
    val x = Rec("myObject" -> "String", "foo" -> "bar")
    val y = x.to[ObjectVal]

    y.myObject should be("String")
  }

  it should "allow conversion if there is a `to` field" in {
    import records._
    val record = Rec("to" -> "R")
    case class ToHolder(to: String)

    record.to should be("R")
    new Rec.Convert(record).to[ToHolder] should be(ToHolder("R"))
  }

  it should "allow explicit conversion even when implict conversion is imported" in {
    import records._
    import records.RecordConversions._
    val record = Rec("field" -> "42")
    case class FieldHolder(field: String)

    record.to[FieldHolder] should be(FieldHolder("42"))
  }

  it should "implicitly convert to a case class in a val position" in {
    import records.RecordConversions._
    val x: DBRecord = Rec("name" -> "David", "age" -> 3, "location" -> "Lausanne")

    x.name should be("David")
  }

  it should "implicitly convert to a case class when constructing a list" in {
    import records.RecordConversions._
    val xs = List[DBRecord](
      Rec("name" -> "David", "age" -> 2, "location" -> "Lausanne"),
      Rec("name" -> "David", "age" -> 3, "location" -> "Lausanne"))

    xs.head.name should be("David")
    xs.tail.head.name should be("David")
  }

}
