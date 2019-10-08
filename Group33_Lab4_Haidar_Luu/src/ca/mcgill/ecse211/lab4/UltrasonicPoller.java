package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.robotics.SampleProvider;

/**
 * Simple UltrasonicPoller that samples the US sensor 
 * and method returns true
 * when obstacle is detected
 * 
 * @author Steven 
 * @author Hassan
 * 
 */
public class UltrasonicPoller {
  private float[] sampleUS;
  SampleProvider distance;
  int wallDistance;
  
  public UltrasonicPoller(SampleProvider distance, float[] sampleUS) {
    this.distance = distance;
    this.sampleUS = sampleUS;
  }
  
  /**
   * 
   * @return float distance in cm
   */
  public float getDistance(){
    distance.fetchSample(sampleUS, 0);
    wallDistance = (int) (sampleUS[0] * 100.0); // extract from buffer, convert to cm, cast to int
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {}
    if(sampleUS[0]>255){
        wallDistance = 255;
    }
    return wallDistance;
}

 

}
