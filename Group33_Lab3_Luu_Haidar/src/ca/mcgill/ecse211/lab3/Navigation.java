package ca.mcgill.ecse211.lab3;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import static ca.mcgill.ecse211.lab3.Resources.TRACK;
import static ca.mcgill.ecse211.lab3.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.lab3.Resources.*;;


public class Navigation {
  
  private static boolean obstacleDetected = true;

  public static void travelTo(double x, double y) {
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);

    obstacleDetected = false;
    x = x * TILE_SIZE;
    y = y * TILE_SIZE;
    double dx = x - odometer.getXYT()[0];
    double dy = y - odometer.getXYT()[1];
    double angle = Math.atan2(dx, dy);

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    turnTo(angle);
    
    double vector = Math.hypot(dx, dy);
    
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    leftMotor.rotate(convertDistance(vector),true);
    rightMotor.rotate(convertDistance(vector),false);
  }

  public static void turnTo(double theta) {
    double angle = theta * 180 /Math.PI - odometer.getXYT()[2];
    leftMotor.rotate(convertAngle(angle), true);
    rightMotor.rotate(-convertAngle(angle), false);
  }

  public boolean isNavigating() {
     return obstacleDetected;
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * From Lab 2 SquareDriver.java
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * From Lab 2 SquareDriver.java
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
}

