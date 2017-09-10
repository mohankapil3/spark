package com.mcl.mcsim.data

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.github.nscala_time.time.Imports._

import scala.io.Source

object Parser {

  def readInvestingDotComHistoryCSV(file: File): Array[(DateTime, Double)] = {

    val format = new SimpleDateFormat("MMM d, yyyy")
    val rawLines = Source.fromFile(file).getLines().toArray
    // first line is header and last 2 lines are to be ignored
    val lines = rawLines.slice(1, rawLines.length - 2)

    lines.map(line => {
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))")
      val dateAsText = removeLeadingTrailingQuotes(cols(0))
      val date = new DateTime(format.parse(dateAsText))
      val value = removeLeadingTrailingQuotes(cols(1)).toDouble
      (date, value)
    }).reverse
  }


  def readGoogleHistoryCSV(file: File): Array[(LocalDate, Double)] = {
    val formatter = DateTimeFormatter.ofPattern("dd-MMM-yy")
    val lines = scala.io.Source.fromFile(file).getLines().toSeq
    lines.tail.map { line =>
      val cols = line.split(',')
      val dateAsText = removeLeadingTrailingQuotes(cols(0))
      val dateWithAsTextWithPaddedZero = if (dateAsText.length == 8) "0" + dateAsText else dateAsText
      val date = LocalDate.parse(dateWithAsTextWithPaddedZero, formatter)
      val priceAsText = cols(1)
      val value = if (priceAsText == "-") 0 else priceAsText.toDouble
      (date, value)
    }.reverse.toArray
  }

  private def removeLeadingTrailingQuotes(data: String): String = {
    val temp = if (data.startsWith("\"")) data.tail else data
    if (temp.endsWith("\"")) temp.substring(0, temp.length - 1) else temp
  }

}
