package com.mcl.mcsim.data

import org.scalatest.{FunSuite, Matchers}

class SourceDataCollectorSuite extends FunSuite with Matchers {

  test("Stocks and factors source data item collections should be of same size") {

    val sourceDataCollector = new SourceDataCollector
    val stocksAndFactorsReturns = sourceDataCollector.readStocksAndFactors()
    val size = stocksAndFactorsReturns._1.head.length
    stocksAndFactorsReturns._1.foreach(_.length should be (size))
    stocksAndFactorsReturns._2.foreach(_.length should be (size))
  }

}