package org.karatachi.scalacl

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.LinkedHashSet

object Profiler {
  /** 試行回数 */
  private val trials = 100
  /** 誤差として結果から破棄する割合 */
  private val truncateRatio = 0.2

  private val results = new LinkedHashMap[String, LinkedHashMap[String, Long]]()

  /**
   * プロファイリングを行う
   *
   * @param title 出力用文字列
   * @param init 初期化ブロック
   * @param block 計測するブロック
   */
  def profile[T](title: String)(init: => T)(block: T => Unit): Unit = {
    System.gc

    var result = List[Long]()

    println("%s: start".format(title))

    var i = trials
    while ({ i -= 1; i >= 0 }) {
      val params = init
      val start = System.nanoTime
      block(params)
      val end = System.nanoTime
      result ::= end - start
    }

    // 上下20%の結果を破棄し平均をとる
    val truncate = (trials * truncateRatio).toInt
    val totalTime = result.sortWith(_ < _).view(truncate, trials - truncate).sum
    val average = totalTime / (trials - truncate * 2) / 1000

    println("%s: %d micro sec".format(title, average))

    val idx = title.lastIndexOf(".")
    val className = title.take(idx)
    val methodName = title.drop(idx)
    val classResults = results.getOrElseUpdate(className, new LinkedHashMap[String, Long]())

    classResults += (methodName -> average)
  }

  /**
   * プロファイル結果を表の形で出力する
   */
  def output(columnsopt: Option[List[String]] = None): Unit = {
    val columns = columnsopt match {
      case Some(m) =>
        m
      case None =>
        val columns = new LinkedHashSet[String]
        results.values.foreach(_.keys.foreach(columns += _))
        columns.toList
    }

    // ヘッダ
    print("|*micro sec|")
    columns.foreach(m => printf("*%s|", m))
    println

    // 結果
    results.keys.foreach { key =>
      print("|" + key + "|")
      columns.foreach(c => printf("%,d|", results(key).getOrElse(c, -1)))
      println
    }
  }
}
