package examples.styles

import org.scalatest.refspec.RefSpec

import scala.collection.immutable.Set

class SetRefSpec extends RefSpec {

  object `A Set` {

    object `when empty` {
      def `should have size 0` {
        assert(Set.empty.size == 0)
      }

      def `should produce NoSuchElementException when head is invoked` {
        assertThrows[NoSuchElementException] {
          Set.empty.head
        }
      }
    }

  }

}
