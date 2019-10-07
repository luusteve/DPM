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
  double wallDistance;
  
  public UltrasonicPoller(SampleProvider distance, float[] sampleUS) {
    this.distance = distance;
    this.sampleUS = sampleUS;
  }
  
  /**

   */
  public float getDistance(){
    distance.fetchSample(sampleUS, 0);
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {}
    if(sampleUS[0]>255){
        sampleUS[0]=255;
    }
    return sampleUS[0];
}

 

}
