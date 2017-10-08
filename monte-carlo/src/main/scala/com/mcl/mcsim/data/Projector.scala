package com.mcl.mcsim.data

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

}
