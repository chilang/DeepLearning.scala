package com.thoughtworks.deeplearning

import cats._
import com.thoughtworks.deeplearning.dsl.ToLayer
import com.thoughtworks.deeplearning.double._
import org.scalatest.{FreeSpec, Matchers}

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
final class ToLayerSpec extends FreeSpec with Matchers {
  "ToLayer" in {
    implicitly[
      ToLayer.OfType[Int, BpDouble#Batch, BpDouble] =:= ToLayer.Aux[Int, BpDouble#Batch, Eval[scala.Double], Eval[scala.Double]]]

    implicitly[
      ToLayer.Aux[Int, BpDouble#Batch, Eval[scala.Double], Eval[scala.Double]] =:= ToLayer.OfType[Int, BpDouble#Batch, BpDouble]]
  }
}
