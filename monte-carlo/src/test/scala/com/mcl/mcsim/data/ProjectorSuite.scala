package com.mcl.mcsim.data

import org.scalatest.{FunSuite, Matchers}

class ProjectorSuite extends FunSuite with Matchers with Projector {

  test("Should transpose matrix correctly") {

    val matrix = Array (
        Array(1, 2),
        Array(3, 4),
        Array(5, 6)
    )

    val expectedTranspose = Array (
        Array(1, 3, 5),
        Array(2, 4, 6)
    )

    transpose(matrix) should be (expectedTranspose)
  }

  test("Should add more factors") {

    val baseFactors = Array(2, 0.16, 9)
    val expected = Array(4.0, 0.0256, 81.0, 1.4142135623730951, 0.4, 3.0, 2.0, 0.16, 9.0)
    val actual = featurize(baseFactors)
    actual should be (expected)
  }

}
