package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private static final double LIGHTSENSOR_THRESHOLD = 40;
  private SampleProvider light = colorSensor.getRedMode();
  private float[] lightData = new float[colorSensor.sampleSize()];
  private int lightValue;

  public boolean correctionTrigger() {
    light.fetchSample(lightData, 0);
    lightValue = (int) (lightData[0] * 100);
    if (lightValue < LIGHTSENSOR_THRESHOLD)
      return true;
    return false;
  }

  /*
   * Here is where the odometer correction code should be run.
   */
  public void run() {
    long correctionStart, correctionEnd;
    while (true) {
      correctionStart = System.currentTimeMillis();
      if (correctionTrigger()) {
        Sound.beep();
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
