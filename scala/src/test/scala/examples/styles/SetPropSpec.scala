package examples.styles

import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

import scala.collection.BitSet
import scala.collection.immutable.{HashSet, TreeSet}


class SetPropSpec extends AnyPropSpec with TableDrivenPropertyChecks with should.Matchers {

  val examples =
    Table(
      "set",
      BitSet.empty,
      HashSet.empty[Int],
      TreeSet.empty[Int]
    )

  property("an empty Set should have size 0") {
    forAll(examples) { set =>
      set.size should be(0)
    }
  }

  property("invoking head on an empty set should produce NoSuchElementException") {
    forAll(examples) { set =>
      a[NoSuchElementException] should be thrownBy {
        set.head
      }
    }
  }
}
