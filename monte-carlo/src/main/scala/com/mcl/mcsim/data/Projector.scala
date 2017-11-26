package com.mcl.mcsim.data

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression

import scala.reflect.ClassTag

trait Projector {

  def transpose[T <: Any : ClassTag](collection: Seq[Array[T]]) : Array[Array[T]] = {
    val result = for (i <- 0 until collection.head.length) yield collection.map(_(i)).toArray
    result.toArray
  }

  def featurize(factorReturns: Array[Double]): Array[Double] = {
    val squareReturns = factorReturns.map(x => math.signum(x) * x * x)
    val squareRootedReturns = factorReturns.map(x => math.signum(x) * math.sqrt(math.abs(x)))
    squareReturns ++ squareRootedReturns ++ factorReturns
  }

  def linearModel(instrument: Array[Double], factorMatrix: Array[Array[Double]]): OLSMultipleLinearRegression = {
    val regression = new OLSMultipleLinearRegression()
    regression.newSampleData(instrument, factorMatrix)
    regression
  }

  def computeFactorWeights(stocksReturns: Seq[Array[Double]], factorFeatures: Array[Array[Double]]): Array[Array[Double]] = {
    stocksReturns.map(linearModel(_, factorFeatures)).map(_.estimateRegressionParameters()).toArray
  }

}
