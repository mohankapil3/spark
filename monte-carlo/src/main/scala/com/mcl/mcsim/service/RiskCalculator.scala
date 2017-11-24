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

    val valueAtRisk = riskCalculator.fivePercentVaR(trials)
    val conditionalValueAtRisk = riskCalculator.fivePercentCVaR(trials)

    println("VaR 5%: " + valueAtRisk)
    println("CVaR 5%: " + conditionalValueAtRisk)
  }

}

class RiskCalculator(private val spark: SparkSession) extends Serializable with Projector {

  private def computeTrialReturns(stocksReturns: Seq[Array[Double]], factorsReturns: Seq[Array[Double]],
           baseSeed: Long, numTrials: Int, parallelism: Int): Dataset[Double] = {

    import spark.implicits._

    val factorMat = transpose(factorsReturns)
    val factorCov = new Covariance(factorMat).getCovarianceMatrix().getData()
    val factorMeans = factorsReturns.map(factor => factor.sum / factor.length).toArray
    val factorFeatures = factorMat.map(featurize)
    val factorWeights = computeFactorWeights(stocksReturns, factorFeatures)

    // Generate different seeds so that our simulations don't all end up with the same results
    val seeds = baseSeed until baseSeed + parallelism
    val seedDS: Dataset[Long] = seeds.toDS().repartition(parallelism)

    // Main computation: run simulations and compute aggregate return for each
    seedDS.flatMap(trialReturns(_, numTrials / parallelism, factorWeights, factorMeans, factorCov))
  }

  private def trialReturns(seed: Long, numTrials: Int, instruments: Seq[Array[Double]],
                   factorMeans: Array[Double], factorCovariances: Array[Array[Double]]): Seq[Double] = {

    val rand = new MersenneTwister(seed)
    val multivariateNormal = new MultivariateNormalDistribution(rand, factorMeans, factorCovariances)

    val trialReturns = new Array[Double](numTrials)
    for (i <- 0 until numTrials) {
      val trialFactorReturns = multivariateNormal.sample()
      val trialFeatures = featurize(trialFactorReturns)
      trialReturns(i) = trialReturn(trialFeatures, instruments)
    }
    trialReturns
  }

  private def trialReturn(trial: Array[Double], instruments: Seq[Array[Double]]): Double = {
    var totalReturn = 0.0
    for (instrument <- instruments) {
      totalReturn += instrumentTrialReturn(instrument, trial)
    }
    totalReturn / instruments.size
  }

  private def instrumentTrialReturn(instrument: Array[Double], trial: Array[Double]): Double = {
    var instrumentTrialReturn = instrument(0)
    var i = 0
    while (i < trial.length) {
      instrumentTrialReturn += trial(i) * instrument(i+1)
      i += 1
    }
    instrumentTrialReturn
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
