package org.karatachi.scalacl

import scala.math._

import Profiler.profile
import scalacl._

object TestWithout {
  def run(): Unit = {
    val gen = (0 until 100000)

    /*
     * 単純なmap/zipWithIndexの使用
     */
    profile("off.nor.map") { gen } { rng =>
      rng.map(_ * 2).zipWithIndex.map(p => p._1 * p._2).sum
    }

    profile("off.par.map") { gen.par } { rng =>
      rng.map(_ * 2).zipWithIndex.map(p => p._1 * p._2).sum
    }

    /*
     * loop内でcos()を呼び出す
     */
    profile("off.nor.loopcos1") { gen } { rng =>
      rng.map { x =>
        var total = 0.0
        for (i <- 0 until 1) { total += cos(x * i) }
        total
      }.sum
    }

    profile("off.par.loopcos1") { gen.par } { rng =>
      rng.map { x =>
        var total = 0.0
        for (i <- 0 until 1) { total += cos(x * i) }
        total
      }.sum
    }

    profile("off.nor.loopcos10") { gen } { rng =>
      rng.map { x =>
        var total = 0.0
        for (i <- 0 until 10) { total += cos(x * i) }
        total
      }.sum
    }

    profile("off.par.loopcos10") { gen.par } { rng =>
      rng.map { x =>
        var total = 0.0
        for (i <- 0 until 10) { total += cos(x * i) }
        total
      }.sum
    }

    profile("off.nor.loopcos100") { gen } { rng =>
      rng.map { x =>
        var total = 0.0
        for (i <- 0 until 100) { total += cos(x * i) }
        total
      }.sum
    }

    profile("off.par.loopcos100") { gen.par } { rng =>
      rng.map { x =>
        var total = 0.0
        for (i <- 0 until 100) { total += cos(x * i) }
        total
      }.sum
    }

    /*
     * 100個の4次のベクトルの内積の計算
     */
    val vec = Array.fill(100) { ((random, random, random, random), (random, random, random, random)) }

    {
      implicit val context = Context.best(CPU)
      profile("off.nor.tuple") { vec } { tuples =>
        tuples.map { t =>
          val (a, b) = t
          a._1 * b._1 + a._2 * b._2 + a._3 * b._3 + a._4 * b._4
        }.sum
      }
    }

    {
      implicit val context = Context.best(CPU)
      profile("off.par.tuple") { vec.par } { array =>
        array.map { t =>
          val (a, b) = t
          a._1 * b._1 + a._2 * b._2 + a._3 * b._3 + a._4 * b._4
        }.sum
      }
    }

    /*
     * forループの高速化
     */
    {
      val n = 100
      profile("off.nor.foreach") {
        (Array.tabulate(n, n)((i, j) => (i + j) * 1.0),
          Array.tabulate(n, n)((i, j) => (i + j) * 1.0),
          Array.ofDim[Double](n, n))
      } {
        case (a, b, o) =>
          for (i <- 0 until n; j <- 0 until n) {
            var tot = 0.0
            for (k <- 0 until n)
              tot += a(i)(k) * b(k)(j)
            o(i)(j) = tot
          }
      }
    }
  }
}
