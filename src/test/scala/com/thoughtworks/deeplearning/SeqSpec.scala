package com.thoughtworks.deeplearning

import cats.Eval
import com.thoughtworks.deeplearning.dsl._
import com.thoughtworks.deeplearning.seq._
import com.thoughtworks.deeplearning.double._
import com.thoughtworks.deeplearning.double.optimizers.LearningRate
import org.scalatest._

import scala.language.existentials

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
final class SeqSpec extends FreeSpec with Matchers {

  implicit def learningRate: LearningRate = new LearningRate {
    override protected def currentLearningRate() = 0.03
  }

  def unsafe(implicit s: BpSeq[BpAny]) = {
    s(0).asInstanceOf[s.To[BpDouble]] - 1.0.toWeight
  }

  "erased BpSeq" in {
    val unsafeNetwork = unsafe
    unsafeNetwork.train(scala.Seq(Eval.now(2.4)))
  }
}
