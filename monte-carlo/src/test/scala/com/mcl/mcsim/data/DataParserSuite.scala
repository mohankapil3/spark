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

  test("Google dot com history CSV file should be parseable") {

    val testHistoryFile = new File(getClass.getResource("/test_stock_prices-google.csv").toURI)
    val tuples = DataParser.readGoogleHistoryCSV(testHistoryFile)
    tuples.length should be (10)
    tuples.head._2 should be (0)
    tuples.last._2 should be (30.80)
    // More assertions?
  }

  test("Google dot com history - SNP index - CSV file should be parseable") {

    val testHistoryFile = new File(getClass.getResource("/test_index_factors_snp.csv").toURI)
    val tuples = DataParser.readGoogleHistoryCSV(testHistoryFile)
    tuples.length should be (10)
    tuples.head._2 should be (85.61)
    tuples.last._2 should be (85.8)
    // More assertions?
  }

}
