package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * Given that the system is placed along an
 * imaginary 45 degree line between (0,0) and (1,1)
 * reorient the system facing NORTH
 * 
 * @author Steven
 * @author Hassan
 *
 */

public class UltrasonicLocalizer {

  private final int type;
  private float[] usData;
  SampleProvider distance;
  private final EV3UltrasonicSensor us;
  private static int wallDistance = 30;
  private static int noiseMargin = 1;
  int filterControl;
  double angle1, angle2, dAngle;


  public UltrasonicLocalizer(int type, EV3UltrasonicSensor us) {
    this.type = type;
    this.us = us;
    this.distance = distance;
  }


/**
 * 
 */
  public void localize() {
    //initialize motors
    leftMotor.stop(true);
    rightMotor.stop(true);
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    if (type == 0) {
      fallingEdge();
    } else if (type == 1) {
      risingEdge();
    }
    //wraparound 359 degree to 0 degree
    if (angle1 > angle2) {
      dAngle = 45 - (angle1 + angle2) / 2;
      Sound.beep();
    } else if (angle1 < angle2) {
      dAngle = 225 - (angle1 + angle2) / 2;
      Sound.buzz();
    } else
      dAngle = angle1 + angle2 / 2;
    //update current odometer angle
    odometer.update(0, 0, dAngle);
    navigate.turnTo(0);
    Button.waitForAnyPress();
  }
  
  /**
   * Record angle and switch direction when wall is detected
   * Initializes robot to start facing the void
   */
  public void fallingEdge() {
    // turn the system until the sensor observes nothing 
    while (Main.UP.getDistance() < wallDistance + noiseMargin) {
      leftMotor.forward();
      rightMotor.backward();
    }
    //turn the system until it sees the back wall
    while (Main.UP.getDistance() > wallDistance - noiseMargin) {
      leftMotor.forward();
      rightMotor.backward();
    }
    //angle at which the sensor sees the back wall
    angle1 = odometer.getXYT()[2];
    //turn the system in the opposite direction until the sensor does not see the wall
    while (Main.UP.getDistance() < wallDistance + noiseMargin) {
      leftMotor.backward();
      rightMotor.forward();
    }
    // turn the system until it sees the left wall 
    while (Main.UP.getDistance() > wallDistance - noiseMargin) {
      leftMotor.backward();
      rightMotor.forward();
    }
    //angle at which the sensor sees the wall  
    angle2 = odometer.getXYT()[2];
    leftMotor.stop(true);
    rightMotor.stop(true);
  }

/**
 * Record angle and switch direction when the sensor detects the
 * transition between wall and nothing
 * Initializes robot to start facing the wall
 */
  public void risingEdge() {
    // turn the system until the sensor observes the left wall 
    while (Main.UP.getDistance() > wallDistance - noiseMargin) {
      leftMotor.backward();
      rightMotor.forward();
    }
    //turn the system until it sees the nothing
    while (Main.UP.getDistance() < wallDistance + noiseMargin) {
      leftMotor.backward();
      rightMotor.forward();
    }
    angle1 = odometer.getXYT()[2];
    // turn the system until the sensor observes the back wall 
    while (Main.UP.getDistance() > wallDistance - noiseMargin) {
      leftMotor.forward();
      rightMotor.backward();
    }
    //turn the system until it sees the nothing
    while (Main.UP.getDistance() < wallDistance + noiseMargin) {
      leftMotor.forward();
      rightMotor.backward();
    }
    angle2 = odometer.getXYT()[2];
    leftMotor.stop(true);
    rightMotor.stop(true);
  }
}
