Advanced Analytics with Spark - Monte Carlo Simulation
======================================================

This practice project has been taken from book "Advanced Analytics with Spark", chapter 9 - "Estimating Financial Risk through Monte Carlo Simulation".

Here we first download historical financial instruments pricing data from below data sources,

  1. http://www.google.com/finance/historical

  2. https://uk.investing.com/commodities/crude-oil-historical-data

Downloaded data has been checked in to folder - src/main/resources/downloaded-data. Ideally this shouldn't be checked in to source code repository but fear is that this FREE data might be unavailable at some time (as happened with yahoo finance).

This data is then used to make Monte Carlo risk models, which will then be used to evaluate VaR (Value at Risk) and CVaR (Conditional Value at Risk).

Brief explanation of processing pipeline,

  1. Data is parsed (see Parser.scala) and collected as Array of date and value tuples.

  2. Parsed data is cleansed (see Cleanser.scala) which involves stuffing with any missing values and then collecting it over a sliding window of two weeks.

  3. SourceDataCollector class combines parser and cleanser to provide unified interface for collecting financial instrument pricing data and market factors.

  4. Then we create predictive models using linear regression (see RiskCalculator.scala). Simplistic approach has been taken, no where near to real world risk models.

  5. Spark SQL DataSet objects are used instead of normal RDD objects to utilize richer aggregation API provided by DataSet objects.

  6. Output is then printed on standard output - VaR and CVaR.
