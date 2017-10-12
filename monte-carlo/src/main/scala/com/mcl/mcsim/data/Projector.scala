package com.mcl.mcsim.data

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression

import scala.reflect.ClassTag

object Projector {

  def transpose[T <: Any : ClassTag](collection: Seq[Array[T]]) : Array[Array[T]] = {
    val newCollectionSize = collection.head.length
    val result = new Array[Array[T]](newCollectionSize)
    for (i <- 0 until newCollectionSize) {
      result(i) = collection.map(_(i)).toArray
    }
    result
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

  def getFactorWeights(stockReturns: Seq[Array[Double]], factorReturns: Seq[Array[Double]]): Array[Array[Double]] = {
    val factorMatrix = transpose(factorReturns)
    val factorFeatures = factorMatrix.map(featurize)
    val models = stockReturns.map(linearModel(_, factorFeatures))
    models.map(_.estimateRegressionParameters()).toArray
  }

}
