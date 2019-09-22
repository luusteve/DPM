package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private static final double LIGHTSENSOR_THRESHOLD = 40;
  
//  recommended by TA
  private static final double LIGHTSENSOR_DELTA = 25;
  
  private SampleProvider light = colorSensor.getRedMode();
  private float[] lightData = new float[colorSensor.sampleSize()];
  private int lightValue;

  // added by Hassan
  Odometer odometer  = Odometer.getOdometer();
  private int INITIAL_LIGHT;
  
  
  
  public boolean correctionTrigger() {
    light.fetchSample(lightData, 0);
    lightValue = (int) (lightData[0] * 100);
    //if (lightValue < LIGHTSENSOR_THRESHOLD)
    //  return true;
    if (Math.abs(lightValue - INITIAL_LIGHT) > LIGHTSENSOR_DELTA)
      return true;
    return false;
  }

  /*
   * Here is where the odometer correction code should be run.
   */
  public void run() {
    long correctionStart, correctionEnd;
    
    // initial color reading
    light.fetchSample(lightData, 0);
    INITIAL_LIGHT = (int) (lightData[0] * 100);
    
    
    int tileCountX = 0;
    int tileCountY = 0;
    while (true) {
      correctionStart = System.currentTimeMillis();
      if (correctionTrigger()) {
        Sound.beep();
        
        double theta = odometer.getXYT()[2];
        // moving in positive y
        if (theta <= 20 || theta >= 340) {
          tileCountY++;
//        odometer.update(0,TILE_SIZE * tileCountY-odometer.getXYT()[1] , 0);
          odometer.setY(TILE_SIZE * tileCountY);
        }
        // moving in positive x
        else if (theta <= 110 && theta >= 80) {
          tileCountX++;
          odometer.setX(TILE_SIZE * tileCountX);
        }
        //moving in negative y
        else if (theta <= 200 && theta >= 160) {
         
          odometer.setY(TILE_SIZE * tileCountY);
          tileCountY--;
        }
        // moving in negative x
        else if(theta <= 290 && theta > 250) {
       
          odometer.setX(TILE_SIZE * tileCountX);
          tileCountX--;
        }
      }
      // TODO Trigger correction (When do I have information to correct?)
      
      
      // colorSensor.set
      // TODO Calculate new (accurate) robot position

      // TODO Update odometer with new calculated (and more accurate) values, eg:
      // odometer.setXYT(0.3, 19.23, 5.0);
      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }

}
