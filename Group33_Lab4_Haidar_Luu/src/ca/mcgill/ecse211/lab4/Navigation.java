package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import static ca.mcgill.ecse211.lab4.Resources.TRACK;
import static ca.mcgill.ecse211.lab4.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.lab4.Resources.*;;

/**
 * Navigates the robot depending on given coordinates avoids obstacles and resumes navigation
 * 
 * @author Steven
 * @author Hassan
 *
 */

public class Navigation{

  public static boolean obstacleDetected = true;
  public static double currentX;
  public static double currentY; 
  
  private static Navigation nav; // Returned as singleton


  /**
   * Orientates robot towards desired destination rotates forward to the coordinate avoids obstacle and resumes when
   * avoided
   * 
   * @param x x coordinate
   * @param y y coordinate
   */

  public void travelTo(double x, double y) {
    // reset and initiliaze motors
    currentX = x;
    currentY = y;
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);

    obstacleDetected = false;
    x = x * TILE_SIZE;
    y = y * TILE_SIZE;
    // Calculate x & y trajectory
    double dx = x - odometer.getXYT()[0];
    double dy = y - odometer.getXYT()[1];
    // Calculate desired angle to turn to in relation to current angle
    double angle = Math.atan2(dx, dy);

    turnTo(angle);
    // Calculate absolute trajectory
    double vector = Math.hypot(dx, dy);

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    leftMotor.rotate(convertDistance(vector), true);
    rightMotor.rotate(convertDistance(vector), true);
  }

  /**
   * turns toward desired angle
   * 
   * @param theta theta in radians
   */
  public static void turnTo(double theta) {
    double thetaDegree = theta * 180 / Math.PI;
    double angle = thetaDegree - odometer.getXYT()[2];
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    // remove negative angles
    if (angle < 0)
      angle += 360;
    // minimal angle
    angle = angle % 360;
    if (angle > 180) {
      leftMotor.rotate(-convertAngle(360 - angle), true);
      rightMotor.rotate(convertAngle(360 - angle), false);
    } else {
      leftMotor.rotate(convertAngle(angle), true);
      rightMotor.rotate(-convertAngle(angle), false);
    }

  }

  /**
   * true when no obstacle detected and motors are moving
   * 
   * @return boolean
   */

  public boolean isNavigating() {
    return !obstacleDetected;
  }


  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance. From Lab 2
   * SquareDriver.java
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that angle. From Lab 2
   * SquareDriver.java
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
  
  /**
   * Returns the Navigation Object. Use this method to obtain an instance of Navigation.
   * 
   * @return the Navigation Object
   */
  public synchronized static Navigation getNavigation() {
    if (nav == null) {
      nav = new Navigation();
    }
    return nav;
  }

}

