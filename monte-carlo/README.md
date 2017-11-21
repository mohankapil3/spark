Advanced Analytics with Spark - Monte Carlo Simulation
======================================================

practise work in progress

Data Sources

  1. http://www.google.com/finance/historical

  2. https://uk.investing.com/commodities/crude-oil-historical-data

  Downloaded data from sources has been checked in to folder - src/main/resources/downloaded-data. Ideally this shouldn't be checked in to source code repository but fear is that this FREE data might be unavailable at some time (as happened with yahoo finance)


Brief explanation of processing pipeline (for details refer - ???)

  1. Data is parsed (see Parser.scala) and collected and as Array of date and value tuples.
  2. Data is then cleansed which involves stuffing with any missing values and then collecting it over a sliding window of two weeks.
  3. Then data is modelled over projected scenarios using linear ????. Please note, this is a mini project and so a very simple ....
  4. DataSets have been used to .
  5. Output is then printed on standard output - Var and CVar
