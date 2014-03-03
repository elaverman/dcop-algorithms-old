package com.signalcollect.dcop.graphstructures

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DimacsParserSpec extends FlatSpec with Matchers {

  "DimacsParser" should "correctly parse a comment" in {
    val parsed = DimacsParser.parseLine("c blalfdlkjre lrkj relkj ", 0)
    assert(parsed == Some(Comment("blalfdlkjre lrkj relkj ")))
  }

  it should "correctly parse an empty comment" in {
    val parsed = DimacsParser.parseLine("c", 0)
    assert(parsed == Some(Comment("")))
  }

  it should "correctly parse metadata" in {
    val parsed = DimacsParser.parseLine("p edge 197 3925", 0)
    assert(parsed == Some(MetaData("edge", 197, 3925)))
  }

  it should "correctly parse edge" in {
    val parsed = DimacsParser.parseLine("e 1 2", 0)
    assert(parsed == Some(EdgeDescriptor(1, 2)))
  }
}
