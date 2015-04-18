package org.uqbar.wollok.choco.actor.traits

import org.uqbar.math.vectors.Touple_to_Vector
import org.uqbar.math.vectors.Vector

/**
 * Intercepts any call to move() and process the real
 * moving with a DeplacementStrategy (interpolation)
 */
trait SmoothMovable extends Actuator { movable =>
  def proceedMove(delta:Vector) { super.move(delta) }
  
  abstract override def move(delta: Vector) {
    val endPosition = translation + delta
    println(s"************** MOVING ($delta) :  ${translation} -> $endPosition")
      
    val x = interpolator(delta, endPosition) { _.x }
    val y = interpolator(delta, endPosition) { _.y }
    
    addAction(new MoveAction(this, new Deplacement(x, y)))
  }
  
  /** override if you want to change the easing function */
  def easingFunction() : Interpolator.EasingFunction = Interpolator.linear

  def interpolator(delta:Vector, endPosition:Vector) (componentExtractor : (Vector)=>Double) = {
    if (componentExtractor(delta) == 0) 
        new ConstantValueGenerator(movable, componentExtractor)
    else
       new InterpolatingValueGenerator(componentExtractor(translation), componentExtractor(endPosition), 2000, easingFunction())
  }
}

class MoveAction(val movable : SmoothMovable, val deplacement: Deplacement) extends Action {
    override def update(d:Double) = { updateInMillis(d * 1000) }
    def updateInMillis(d: Double) = {
      val newDelta = deplacement.nextPosition(d) - movable.translation;
//      println(s"\t\t${movable.translation} -> ${movable.translation + newDelta}...")
      movable.proceedMove(newDelta)
      deplacement.continue()
    }
}