package com.thoughtworks.deeplearning

import com.thoughtworks.deeplearning.dsl.{ToLayer, BackPropagationType}
import com.thoughtworks.deeplearning.dsl.BackPropagationType.{DataOf, DeltaOf}
import com.thoughtworks.deeplearning.hlist.layers._

import scala.language.implicitConversions
import scala.language.existentials

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
package object hlist {

  /** @template */
  type BpHList = BackPropagationType[_ <: shapeless.HList, _ <: shapeless.Coproduct]

  /** @template */
  type BpHNil = BackPropagationType[shapeless.HNil, shapeless.CNil]

  /** @template */
  type BpHCons[Head <: BackPropagationType[_, _], Tail <: BpHList] =
    BackPropagationType[shapeless.::[DataOf[Head], DataOf[Tail]], shapeless.:+:[DeltaOf[Head], DeltaOf[Tail]]]

  /** @template */
  type :**:[Head <: BackPropagationType[_, _], Tail <: BpHList] =
    BackPropagationType[shapeless.::[DataOf[Head], DataOf[Tail]], shapeless.:+:[DeltaOf[Head], DeltaOf[Tail]]]

  val BpHNil: layers.HNil.type = layers.HNil

  implicit def hnilToLayer[InputData, InputDelta](implicit inputType: BackPropagationType[InputData, InputDelta])
    : ToLayer.Aux[layers.HNil.type, Batch.Aux[InputData, InputDelta], shapeless.HNil, shapeless.CNil] =
    new ToLayer[layers.HNil.type, Batch.Aux[InputData, InputDelta]] {
      override type OutputData = shapeless.HNil
      override type OutputDelta = shapeless.CNil

      override def apply(hnil: layers.HNil.type) = hnil
    }

  final class HListOps[Input <: Batch, TailData <: shapeless.HList, TailDelta <: shapeless.Coproduct](
      tail: Layer.Aux[Input, Batch.Aux[TailData, TailDelta]]) {

    def ::[Head, HeadData, HeadDelta](head: Head)(implicit headToLayer: ToLayer.Aux[Head, Input, HeadData, HeadDelta])
    : Layer.Aux[Input, Batch.Aux[shapeless.::[HeadData, TailData], shapeless.:+:[HeadDelta, TailDelta]]] = {
      HCons[Input, HeadData, HeadDelta, TailData, TailDelta](headToLayer(head), tail)
    }

    def :**:[Head, HeadData, HeadDelta](head: Head)(implicit headToLayer: ToLayer.Aux[Head, Input, HeadData, HeadDelta])
      : Layer.Aux[Input, Batch.Aux[shapeless.::[HeadData, TailData], shapeless.:+:[HeadDelta, TailDelta]]] = {
      HCons[Input, HeadData, HeadDelta, TailData, TailDelta](headToLayer(head), tail)
    }

  }

  implicit def toHListOps[From, Input <: Batch, TailData <: shapeless.HList, TailDelta <: shapeless.Coproduct](
      from: From)(
      implicit toLayer: ToLayer.Aux[From, Input, TailData, TailDelta]
  ): HListOps[Input, TailData, TailDelta] = {
    new HListOps[Input, TailData, TailDelta](toLayer(from))
  }

  final class HConsOps[Input <: Batch, HeadData, HeadDelta, TailData <: shapeless.HList,
  TailDelta <: shapeless.Coproduct](
      hcons: Layer.Aux[Input, Batch.Aux[shapeless.::[HeadData, TailData], shapeless.:+:[HeadDelta, TailDelta]]]) {
    def head: Layer.Aux[Input, Batch.Aux[HeadData, HeadDelta]] =
      Head[Input, HeadData, HeadDelta, TailData, TailDelta](hcons)

    def tail: Layer.Aux[Input, Batch.Aux[TailData, TailDelta]] =
      Tail[Input, HeadData, HeadDelta, TailData, TailDelta](hcons)
  }

  implicit def toHConsOps[From,
                          Input <: Batch,
                          OutputData,
                          OutputDelta,
                          HeadData,
                          HeadDelta,
                          TailData <: shapeless.HList,
                          TailDelta <: shapeless.Coproduct](from: From)(
      implicit toLayer: ToLayer.Aux[From, Input, OutputData, OutputDelta],
      toHListLayer: Layer.Aux[Input, Batch.Aux[OutputData, OutputDelta]] <:< Layer.Aux[
        Input,
        Batch.Aux[shapeless.::[HeadData, TailData], shapeless.:+:[HeadDelta, TailDelta]]]
  ): HConsOps[Input, HeadData, HeadDelta, TailData, TailDelta] = {
    new HConsOps[Input, HeadData, HeadDelta, TailData, TailDelta](toHListLayer(toLayer(from)))
  }

}
