package org.karatachi.scalacl

import Profiler.output

object Bootstrap {
  def main(args: Array[String]): Unit = {
    TestWith.run
    TestWithout.run

    output()
  }
}
