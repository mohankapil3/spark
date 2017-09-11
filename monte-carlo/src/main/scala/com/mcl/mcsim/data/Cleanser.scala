package com.mcl.mcsim.data

import java.time.LocalDate

import scala.collection.mutable.ArrayBuffer

object Cleanser {

  def trimToRegion(history: Array[(LocalDate, Double)], start: LocalDate, end: LocalDate): Array[(LocalDate, Double)] = {
    var trimmed = history.dropWhile(_._1.isBefore(start)).takeWhile(x => x._1.isBefore(end) || x._1.isEqual(end))
    if (trimmed.head._1 != start) {
      trimmed = Array((start, trimmed.head._2)) ++ trimmed
    }
    if (trimmed.last._1 != end) {
      trimmed = trimmed ++ Array((end, trimmed.last._2))
    }
    trimmed
  }

  def fillInHistory(history: Array[(LocalDate, Double)], start: LocalDate, end: LocalDate): Array[(LocalDate, Double)] = {
    var cur = history
    val filled = new ArrayBuffer[(LocalDate, Double)]()
    var curDate = start
    while (!curDate.isAfter(end)) {
      if (cur.tail.nonEmpty && cur.tail.head._1 == curDate) {
        cur = cur.tail
      }

      filled += ((curDate, cur.head._2))

      curDate = curDate.plusDays(1)
      // Skip weekends
      if (curDate.getDayOfWeek.getValue > 5) {
        curDate = curDate.plusDays(2)
      }
    }
    filled.toArray
  }

  def twoWeekReturns(history: Array[(LocalDate, Double)]): Array[Double] = {
    history.sliding(10).map { window =>
      val end = window.last._2
      val start = window.head._2
      (end - start) / start
    }.toArray
  }

}
