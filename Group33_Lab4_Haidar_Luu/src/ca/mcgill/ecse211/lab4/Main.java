package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import ca.mcgill.ecse211.lab4.UltrasonicPoller;
import ca.mcgill.ecse211.lab4.Display;
import lejos.hardware.Button;
import lejos.robotics.SampleProvider;

public class Main {
  static SampleProvider distance = US_SENSOR.getMode("Distance");
  static float[] sampleUS = new float[distance.sampleSize()];
  final static UltrasonicPoller UP = new UltrasonicPoller(distance, sampleUS);
  public static void main(String[] args) {
    UltrasonicLocalizer localize;
    int option;
    option = chooseEdge();
    new Thread(new Display()).start();
    new Thread(odometer).start();
    if (option == Button.ID_LEFT) {
      localize = new UltrasonicLocalizer(0, US_SENSOR);
      Thread localizeThread = new Thread(localize);
      localizeThread.start();
    } else if (option == Button.ID_RIGHT) {
      localize = new UltrasonicLocalizer(1, US_SENSOR);
      Thread localizeThread = new Thread(localize);
       localizeThread.start();
    } else {
      showErrorAndExit("Error - invalid button!");
      
    }
    

    //while (true) System.out.println(option);
  }

  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  /**
   * Shows error and exits program.
   */
  public static void showErrorAndExit(String errorMessage) {
    LCD.clear();
    System.err.println(errorMessage);
    
    // Sleep for 2 seconds so user can read error message
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
    }
    
    System.exit(-1);
  }
  /**
   * Asks the user whether odometry correction should be run or not.
   * 
   * @return the user choice
   */
  private static int chooseEdge() {
    int buttonChoice;
    Display.showText("< Left | Right >",
                     "falling| rising ",
                     " edge  |  edge  ",
                     "       |        ",
                     "       |        ");

    do {
      buttonChoice = Button.waitForAnyPress();
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;
  }
}
