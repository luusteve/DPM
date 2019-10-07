package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class UltrasonicLocalizer extends Thread {

  private final int type;
  private float[] usData;
  SampleProvider distance;
  private final EV3UltrasonicSensor us;
  int filterControl;
  double angle1, angle2;


  public void run() {
    localize();
  }

  public UltrasonicLocalizer(int type ,EV3UltrasonicSensor us) {
    this.type = type;
    this.us = us;
    this.distance = distance;
  }



  public void localize() {

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
     if (type == 0) {
     fallingEdge();
     }
     else if (type == 1) {
     risingEdge();
     }
  }

  public void fallingEdge() {
    while (Main.UP.getDistance() < 43) {
      leftMotor.forward();
      rightMotor.backward();
    }
    while (Main.UP.getDistance() > 37) {
      leftMotor.forward();
      rightMotor.backward();
    }
    angle1 = odometer.getXYT()[2];

    while (Main.UP.getDistance() < 43) {
      leftMotor.backward();
      rightMotor.forward();
    }
    while (Main.UP.getDistance() > 37) {
      leftMotor.backward();
      rightMotor.forward();
    }
    leftMotor.stop(true);
    rightMotor.stop(true);
    
    angle2 = odometer.getXYT()[2];
    
    double avgAngle;
    if (angle1 > angle2) {
      angle1 = 225 - (angle1 + angle2)/2;
      avgAngle = 45 - (angle1 + angle2) / 2;
    }
    else avgAngle = 255 - (angle1 + angle2)/2;
    
    double ZeroPoint = angle1 - avgAngle + 45;
    odometer.setXYT(ZeroPoint % TILE_SIZE, ZeroPoint % TILE_SIZE, avgAngle);
    navigate.travelTo(1,1);
  }

  public void risingEdge() {

  }

  public float filter(float dist) {
    if (dist > 50 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the filter value
      filterControl++;
      return 100;
    } else if (dist >= 50) {
      // Repeated large values, so there is nothing there: leave the distance alone
      return 50;
    } else {
      // distance went below 50: reset filter and leave distance alone.
      filterControl = 0;
      return dist;
    }
  }
}
