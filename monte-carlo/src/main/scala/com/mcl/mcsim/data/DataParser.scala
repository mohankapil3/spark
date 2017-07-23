package com.mcl.mcsim.data

import java.io.File
import java.text.SimpleDateFormat

import com.github.nscala_time.time.Imports._

import scala.io.Source

object DataParser {

  def readInvestingDotComHistoryCSV(file: File): Array[(DateTime, Double)] = {

    val format = new SimpleDateFormat("MMM d, yyyy")
    val rawLines = Source.fromFile(file).getLines().toArray
    // first line is header and last 2 lines are to be ignored
    val lines = rawLines.slice(1, rawLines.length - 2)

    lines.map(line => {
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))")
      val dataAsText = removeLeadingTrailingQuotes(cols(0))
      val date = new DateTime(format.parse(dataAsText))
      val value = removeLeadingTrailingQuotes(cols(1)).toDouble
      (date, value)
    }).reverse
  }

  private def removeLeadingTrailingQuotes(data: String): String = {
    val temp = if (data.startsWith("\"")) data.tail else data
    if (temp.endsWith("\"")) temp.substring(0, temp.length - 1) else temp
  }

}
