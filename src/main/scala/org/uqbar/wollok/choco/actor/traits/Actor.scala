package org.uqbar.wollok.choco.actor.traits

import org.uqbar.chocolate.core.appearances.Animation
import org.uqbar.chocolate.core.components.Visible
import org.uqbar.chocolate.core.dimensions.Positioned
import org.uqbar.chocolate.core.reactions.events.MouseMoved
import org.uqbar.chocolate.core.reactions.events.Pressed
import org.uqbar.chocolate.core.reactions.io.Key.Letter.A
import org.uqbar.chocolate.core.reactions.io.Key.Letter.D
import org.uqbar.chocolate.core.reactions.io.Key.Letter.S
import org.uqbar.chocolate.core.reactions.io.Key.Letter.W
import org.uqbar.chocolate.core.reactions.io.Key.Navigation.Arrow.Down
import org.uqbar.chocolate.core.reactions.io.Key.Navigation.Arrow.Left
import org.uqbar.chocolate.core.reactions.io.Key.Navigation.Arrow.Right
import org.uqbar.chocolate.core.reactions.io.Key.Navigation.Arrow.Up
import org.uqbar.chocolate.core.reactions.io.MouseButton.{Left => MouseLeft}
import org.uqbar.math.vectors.MutableVector
import org.uqbar.math.vectors.Touple_to_Vector
import org.uqbar.math.vectors.Vector

trait Actor extends Visible with Positioned {
}

trait BaseMovement extends Actor {
  in {
     case Pressed(W) ⇒ moveUp()
     case Pressed(Up(_)) ⇒ moveUp()
     
     case Pressed(S) ⇒ moveDown()
     case Pressed(Down(_)) ⇒ moveDown()
     
     case Pressed(A) ⇒ moveLeft()
     case Pressed(Left(_)) ⇒ moveLeft()
     
     case Pressed(Right(_)) ⇒ moveRigth()
     case Pressed(D) ⇒ moveRigth()
  }
  def moveUp()
  def moveDown()
  def moveLeft()
  def moveRigth()
  
  def velocity : Double
}

/**
 * Tracks directional keyboards and moves the player: up, down, left, right.
 * 
 * @author jfernandes
 */
trait MovesWithKeyboard extends BaseMovement {
  override def moveUp() { move(0, -velocity) }
  override def moveDown() { move(0, velocity) }
  override def moveLeft() { move(-velocity, 0) }
  override def moveRigth() { move(velocity, 0) }
}

/**
 * Track the mouse movement and moves the actor to the same
 * place.
 * 
 * @author jfernandes
 */
trait FollowMouse extends Actor {
  in { case MouseMoved(position) => move(position - this.translation) }
}

/**
 * Like graphical adventures, the actor will move to the mouse current's position
 * on a click.
 */
trait FollowMouseClick extends Actor {
  //TODO: interpolate
  protected var _mousePosition: Vector = (0, 0)
  in { 
    case MouseMoved(position) => _mousePosition = position
    case Pressed(MouseLeft) =>  move(_mousePosition - this.translation)
  }
}

/**
 * Tracks directions on keyboard to move the actor.
 * But the movement rotational. Left and Right arrows will rotate the actor, while up and down
 * will make it go forewards or backwards.
 */
trait RotationalMovement extends BaseMovement {
  //TODO: avoid rotation the already rotated sprites because it will screw up the image.
  // use the original one.
  var angle = Math.PI / 2
  var rotationVelocity = 10
  
  override def moveLeft() { rotate(-rotationVelocity) }
  override def moveRigth() { rotate(rotationVelocity) }
  
  def rotate(delta: Double) {
    angle += Math.toRadians(delta)
    appearance match  {
      case a: Animation => a.rotate(Math.toRadians(delta)) 
    }
  }

  override def moveUp() { ahead(-1d) }
  override def moveDown() { ahead(1d) }
  
  def ahead(direction : Double) {
    this.move(direction * velocity * Math.cos(angle), direction * velocity * Math.sin(angle))
  }
}