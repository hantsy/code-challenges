package examples.base

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{Inside, Inspectors, OptionValues}


abstract class UnitSpec extends AnyFlatSpec with should.Matchers with
  OptionValues with Inside with Inspectors