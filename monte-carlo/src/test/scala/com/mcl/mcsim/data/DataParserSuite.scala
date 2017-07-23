package com.mcl.mcsim.data

import java.io.File

import org.scalatest.{FunSuite, Matchers}

class DataParserSuite extends FunSuite with Matchers {

  test("Investing dot com history CSV file should be parseable") {

    val testHistoryFile = new File(getClass.getResource("/test_Crude_Oil_Historical_Prices-Investing.com-UK.csv").toURI)
    val tuples = DataParser.readInvestingDotComHistoryCSV(testHistoryFile)
    tuples.length should be (21)
    tuples.last._2 should be (45.77)
    // More assertions?
  }

}
