package com.mcl.mcsim.data

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}

class CleanserSuite extends FunSuite with Matchers {

  test("History items outside the range should be dropped") {
    val start = LocalDate.of(2016, 12, 31)
    val end = LocalDate.of(2017, 1, 15)
    val itemsWithEarlierStartDate = Array((LocalDate.of(2016, 12, 29), 5.67))
    val itemsWithInRange = Array(
      (LocalDate.of(2016, 12, 31), 6.34),
      (LocalDate.of(2017, 1, 5), 3.22),
      (LocalDate.of(2017, 1, 15), 8.54)
    )
    val itemsWithLaterEndDate = Array((LocalDate.of(2017, 1, 17), 3.34))
    val items = itemsWithEarlierStartDate ++ itemsWithInRange ++ itemsWithLaterEndDate

    val trimmed = Cleanser.trimToRegion(items, start, end)
    trimmed should be (itemsWithInRange)
  }

  test("Missing history items on start and end date should be added") {
    val start = LocalDate.of(2016, 12, 31)
    val end = LocalDate.of(2017, 1, 15)
    val items = Array(
      (LocalDate.of(2017, 1, 1), 6.34),
      (LocalDate.of(2017, 1, 5), 3.22),
      (LocalDate.of(2017, 1, 14), 8.54)
    )
    val itemsAfterStuffing = Array((start, 6.34)) ++ items ++ Array((end, 8.54))

    val stuffed = Cleanser.trimToRegion(items, start, end)
    stuffed should be (itemsAfterStuffing)
  }

  test("Missing history items between start and end should be filled (only for weekdays)") {
    val start = LocalDate.of(2016, 12, 30)
    val end = LocalDate.of(2017, 1, 13)
    val items = Array(
      (start, 6.34),
      (end, 8.54)
    )

    val stuffed = Cleanser.fillInHistory(items, start, end)
    stuffed.length should be (11)
    stuffed.init.foreach(_._2 should be (6.34))
    stuffed.last should be ((end, 8.54))
  }

  test("Calculate change in value over two weeks") {
    val start = LocalDate.of(2016, 12, 30)
    val end = LocalDate.of(2017, 1, 13)
    val items = Array(
      (start, 2.0),
      (end, 10.0)
    )

    val stuffed = Cleanser.fillInHistory(items, start, end)
    val twoWeeksReturn = Cleanser.twoWeekReturns(stuffed)
    twoWeeksReturn should be (Array(0, 8))
  }

}
