package com.mcl.mcsim.service

import com.mcl.mcsim.data.{Projector, SourceDataCollector}
import org.apache.commons.math3.distribution.MultivariateNormalDistribution
import org.apache.commons.math3.random.MersenneTwister
import org.apache.commons.math3.stat.correlation.Covariance
import org.apache.spark.sql.{Dataset, SparkSession}

object RiskCalculator {

  def main(args: Array[String]): Unit = {
    val sourceDataCollector = new SourceDataCollector
    val (stocksReturns, factorsReturns) = sourceDataCollector.readStocksAndFactors()

    val sparkSession = SparkSession.builder().appName("RiskCalculator").config("spark.master", "local").getOrCreate()
    val riskCalculator = new RiskCalculator(sparkSession)

    val numTrials = 100000
    val parallelism = 8
    val baseSeed = 1001L

    val trials: Dataset[Double] = riskCalculator.computeTrialReturns(stocksReturns, factorsReturns, baseSeed, numTrials, parallelism)

    trials.cache()

    val valueAtRisk = fivePercentVaR(trials)
    val conditionalValueAtRisk = fivePercentCVaR(trials)

    println("Value at Risk (VaR 5%): " + valueAtRisk)
    println("Conditional Value at Risk (CVaR 5%): " + conditionalValueAtRisk)
  }

  private def fivePercentVaR(trials: Dataset[Double]): Double = {
    val quantiles = trials.stat.approxQuantile("value", Array(0.05), 0.0)
    quantiles.head
  }

  private def fivePercentCVaR(trials: Dataset[Double]): Double = {
    val topLosses = trials.orderBy("value").limit(math.max(trials.count().toInt / 20, 1))
    topLosses.agg("value" -> "avg").first()(0).asInstanceOf[Double]
  }

}

class RiskCalculator(private val spark: SparkSession) extends Serializable with Projector {

  private def computeTrialReturns(stocksReturns: Seq[Array[Double]], factorsReturns: Seq[Array[Double]],
           baseSeed: Long, numTrials: Int, parallelism: Int): Dataset[Double] = {

    import spark.implicits._

    val factorMatrix = transpose(factorsReturns)
    val factorCovariance = new Covariance(factorMatrix).getCovarianceMatrix().getData()
    val factorMeans = factorsReturns.map(factor => factor.sum / factor.length).toArray
    val factorFeatures = factorMatrix.map(featurize)
    val factorWeights = computeFactorWeights(stocksReturns, factorFeatures)

    // Generate different seeds so that our simulations don't all end up with the same results
    val seeds = baseSeed until baseSeed + parallelism
    val seedDS: Dataset[Long] = seeds.toDS().repartition(parallelism)

    // Main computation: run simulations and compute aggregate return for each
    seedDS.flatMap(trialReturns(_, numTrials / parallelism, factorWeights, factorMeans, factorCovariance))
  }

  private def trialReturns(seed: Long, numTrials: Int, instruments: Seq[Array[Double]],
                   factorMeans: Array[Double], factorCovariances: Array[Array[Double]]): Seq[Double] = {

    val randomGenerator = new MersenneTwister(seed)
    val multivariateNormal = new MultivariateNormalDistribution(randomGenerator, factorMeans, factorCovariances)

    (1 to numTrials).map ( _ => {
      val trialFactorReturns = multivariateNormal.sample()
      val trialFeatures = featurize(trialFactorReturns)
      trialReturn(trialFeatures, instruments)
    })
  }

  private def trialReturn(trial: Array[Double], instruments: Seq[Array[Double]]): Double = {
    val totalReturn = instruments.map(i => instrumentTrialReturn(i, trial)).reduce((r1, r2) => r1 + r2)
    totalReturn / instruments.size
  }

  private def instrumentTrialReturn(instrument: Array[Double], trial: Array[Double]): Double = {
    val returns = for (i <- 0 until trial.length) yield trial(i) * instrument(i+1)
    instrument(0) + returns.reduce((r1, r2) => r1 + r2)
  }

}
