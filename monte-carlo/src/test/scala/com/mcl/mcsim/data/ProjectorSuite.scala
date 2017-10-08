package com.mcl.mcsim.data

import org.scalatest.{FunSuite, Matchers}

class ProjectorSuite extends FunSuite with Matchers {

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

    Projector.transpose(matrix) should be (expectedTranspose)
  }

}
