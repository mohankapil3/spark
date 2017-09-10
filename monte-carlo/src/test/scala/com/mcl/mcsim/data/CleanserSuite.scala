package com.mcl.mcsim.data

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}

class CleanserSuite extends FunSuite with Matchers {

  test("Trim to region should drop items outside range and add start and end") {
    val start = LocalDate.of(2016, 12, 31)
    val end = LocalDate.of(2017, 1, 15)
    val items = Array(
      (LocalDate.of(2016, 12, 29), 5.67),
      (LocalDate.of(2016, 12, 31), 6.34),
      (LocalDate.of(2017, 1, 5), 3.22),
      (LocalDate.of(2017, 1, 15), 8.54),
      (LocalDate.of(2017, 1, 17), 3.34)
    )

    val expectedTrimmedItems = Array(
      (LocalDate.of(2016, 12, 31), 6.34),
      (LocalDate.of(2017, 1, 5), 3.22),
      (LocalDate.of(2017, 1, 15), 8.54)
    )

    val trimmed = Cleanser.trimToRegion(items, start, end)
    trimmed should be (expectedTrimmedItems)
  }

}
