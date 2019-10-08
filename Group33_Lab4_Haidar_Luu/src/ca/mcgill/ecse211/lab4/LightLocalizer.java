package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import ca.mcgill.ecse211.lab4.Main;
import ca.mcgill.ecse211.lab4.Odometer;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Light localization that assumes that the device is facing NORTH and is on a 45 degree line in between (0,0) and (1,1)
 * 
 * @author Steven
 * @author Hassan
 */

public class LightLocalizer {
  private static final long CORRECTION_PERIOD = 10;
  private static final double LIGHTSENSOR_DELTA = 30;
  long correctionStart, correctionEnd;
  private SampleProvider light = colorSensor.getRedMode();
  private float[] lightData = new float[colorSensor.sampleSize()];
  private int lightValue;
  private int count;
  // Our device has a 6.4 cm distance between it's center and the light sensor
  // The light sensor is placed at the front
  private double offSet = 6.4;
  private double thetaXa, thetaXb, thetaYa, thetaYb, thetaX, thetaY, x, y;
  private int INITIAL_LIGHT;

  /**
   * @return returns true if black line detected, else false
   */
  public boolean correctionTrigger() {
    light.fetchSample(lightData, 0);
    lightValue = (int) (lightData[0] * 100);
    if (Math.abs(lightValue - INITIAL_LIGHT) > LIGHTSENSOR_DELTA) {
      Sound.twoBeeps();
      return true;
    }
    return false;
  }

  /**
   * main method assuming that the device is facing north turn 45 degree and make sure that we are not already on (1,1)
   * by starting off with a spin
   */
  public void localize() {
    // initial light data that will be used to detect black lines
    light.fetchSample(lightData, 0);
    INITIAL_LIGHT = (int) (lightData[0] * 100);
    navigate.turnTo(45 * Math.PI / 180);
    // make sure we are not already on a cross section
    while (count != 4) {
      spinSearch();
      // if we are on a cross section, 4 lines should be detected
      if (count == 4) {
        // calculate device's current X and Y position
        //with 0,0 as it's origin
        thetaX = (thetaXb - thetaXa);
        thetaY = (thetaYb - thetaYa);
        x = -offSet * Math.cos(thetaY / 2 * Math.PI / 180);
        y = -offSet * Math.cos(thetaX / 2 * Math.PI / 180);
        break;
      }

      while (true) {
        correctionStart = System.currentTimeMillis();
        // while facing 45 degree, go forward until a black line is detected
        leftMotor.forward();
        rightMotor.forward();
        // continue going, to take into account the offset
        if (correctionTrigger()) {
          leftMotor.stop(true);
          rightMotor.stop(true);
          leftMotor.rotate(convertDistance(offSet), true);
          rightMotor.rotate(convertDistance(offSet), false);
          break;
        }
        // this ensures the line detection occurs only once every period
        correctionEnd = System.currentTimeMillis();
        if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
          Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        }
      }
    }
    //x and y are around (0,0) add TILE_SIZE
    odometer.setX(TILE_SIZE + x);
    odometer.setY(TILE_SIZE + y);
    navigate.travelTo(1, 1);
    //face NORTH
    navigate.turnTo(0);
    leftMotor.stop();
    rightMotor.stop();
  }

  public void spinSearch() {
    //initialize motors
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    //turn around itself clockwise
    leftMotor.rotate(convertAngle(360), true);
    rightMotor.rotate(-convertAngle(360), true);
    count = 0;
    //exits when self rotation is completed
    while (leftMotor.isMoving() || rightMotor.isMoving()) {
      correctionStart = System.currentTimeMillis();
      //look for black lines
      if (correctionTrigger()) {
        count += 1;
        //since device is 45 degree from line
        //first line detected is small X
        if (count == 1) {
          thetaXa = odometer.getXYT()[2];
          //second line detected is small Y
        } else if (count == 2) {
          thetaYa = odometer.getXYT()[2];
          //third line detected is big X
        } else if (count == 3) {
          thetaXb = odometer.getXYT()[2];
          //fourth line detected is big Y 
        } else if (count == 4) {
          thetaYb = odometer.getXYT()[2];
        }
        // this ensures the line detection occurs only once every period
        correctionEnd = System.currentTimeMillis();
        if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
          Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        }
      }
    }
    Sound.beepSequenceUp();
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that angle.
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }

}

