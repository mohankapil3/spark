package com.mcl.mcsim.data

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}

class CleanserSuite extends FunSuite with Matchers {

  test("Trim to region should drop items outside the range") {
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

  test("Trim to region should add start and end items if missing") {
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

}
