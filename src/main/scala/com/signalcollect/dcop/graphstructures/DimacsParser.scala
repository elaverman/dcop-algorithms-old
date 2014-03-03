package com.signalcollect.dcop.graphstructures

import java.io.File
import scala.util.parsing.input.CharArrayReader
import scala.util.parsing.combinator.Parsers

sealed trait DimacsEntity

case class Comment(text: String) extends DimacsEntity
case class MetaData(format: String, nodes: Int, edges: Int) extends DimacsEntity
case class NodeDescriptor(id: Int, value: Int) extends DimacsEntity
case class EdgeDescriptor(source: Int, target: Int) extends DimacsEntity

object DimacsParser {
  def parseLine(l: String, lineNumber: Long): Option[DimacsEntity] = {
    val split = l.split(" ")
    if (split.isEmpty) {
      println(s"Line $lineNumber was empty")
      None
    } else {
      val linePrefix = split(0)
      try {
        linePrefix match {
          case "c" => if (split.size > 1) Some(Comment(l.tail.tail)) else Some(Comment(""))
          case "p" => Some(MetaData(split(1), split(2).toInt, split(3).toInt))
          case "n" => Some(NodeDescriptor(split(1).toInt, split(2).toInt))
          case "e" => Some(EdgeDescriptor(split(1).toInt, split(2).toInt))
          case other => {
            println(s"Line $lineNumber had unsupported prefix $other")
            None
          }
        }
      } catch {
        case t: Throwable => throw new Exception(s"Error when parsing line $lineNumber with prefix ${split(0)} and rest ${l.tail.tail}")
      }
    }
  }

  def parse(f: File): Traversable[DimacsEntity] = {
    val source = io.Source.fromFile(f)
    source.getLines.zipWithIndex.flatMap {
      case (line, index) =>
        parseLine(line, index)
    }.toTraversable
  }

}