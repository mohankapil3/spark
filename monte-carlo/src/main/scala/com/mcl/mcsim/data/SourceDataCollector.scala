package com.mcl.mcsim.data

import java.io.File
import java.time.LocalDate

object SourceDataCollector {

  private val START_DATE = LocalDate.of(2009, 10, 23)
  private val END_DATE = LocalDate.of(2014, 10, 23)
  private val STOCKS_DIR = getFileFromClassPath("/downloaded-data/stocks")
  private val FACTORS_DIR = "/downloaded-data/factors"

  def readStocksAndFactors: (Seq[Array[Double]], Seq[Array[Double]]) = {
    val allStockFiles = STOCKS_DIR.listFiles()
    val allStocks = allStockFiles.flatMap { file =>
      try {
        Some(Parser.readGoogleHistoryCSV(file))
      } catch {
        case _: RuntimeException => None
      }
    }

    val rawStocks = allStocks.filter(_.length >= 240 * 5)
    val stocks = rawStocks.map(Cleanser.trimToRegion(_, START_DATE, END_DATE))
                          .map(Cleanser.fillInHistory(_, START_DATE, END_DATE))

    val crudeOilRawFactors = Parser.readInvestingDotComHistoryCSV(getFileFromClassPath(FACTORS_DIR + "/crude_oil.csv"))
    val snpRawFactors = Parser.readGoogleHistoryCSV(getFileFromClassPath(FACTORS_DIR + "/snp.csv"))
    val rawFactors = Array(crudeOilRawFactors,  snpRawFactors)
    val factors = rawFactors.map(Cleanser.trimToRegion(_, START_DATE, END_DATE))
                            .map(Cleanser.fillInHistory(_, START_DATE, END_DATE))

    val stocksReturns = stocks.map(Cleanser.twoWeekReturns)
    val factorsReturns = factors.map(Cleanser.twoWeekReturns)
    (stocksReturns, factorsReturns)
  }

  private def getFileFromClassPath(path: String) = new File(getClass.getResource(path).toURI)

}
