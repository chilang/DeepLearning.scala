package com.thoughtworks.deepLearning

import com.thoughtworks.deepLearning.Ast._
import com.thoughtworks.deepLearning.Batch._
import com.thoughtworks.deepLearning.Batch.WidenBatch
import scala.language.higherKinds

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
object Batch {

  /** @template */
  type WidenBatch[+Data0, -Delta0] = Batch {
    type Data <: Data0
    type Delta >: Delta0
  }
}

trait Batch extends AutoCloseable { outer =>
  type Data
  type Delta

  /**
    * @note This is a workaround for https://issues.scala-lang.org/browse/SI-10008
    * @template
    */
  type Widen >: WidenBatch[Data, Delta] <: WidenBatch[Data, Delta]

  /**
    * @note This is a workaround for https://issues.scala-lang.org/browse/SI-10008
    * @template
    */
  type ToWidenAst[Output <: Batch] >: WidenAst[Widen, Output#Widen] <: WidenAst[Widen, Output#Widen]

  def widen: Widen = this: WidenBatch[Data, Delta]

  def backward(delta: Delta): Unit

  def value: Data
}
