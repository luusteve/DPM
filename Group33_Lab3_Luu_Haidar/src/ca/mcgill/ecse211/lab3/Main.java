package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import ca.mcgill.ecse211.lab3.Display;
import lejos.hardware.Button;


public class Main {
  public static Navigation navigator = new Navigation();

  public static void main(String[] args) {
    new Thread(odometer).start();
    new Thread(new Display()).start();
    Button.waitForAnyPress();
    Navigation.travelTo(3, 2);
  }
  
  
}
