package evosteer;

import evosteer.util.Utils;
import processing.core.PApplet;

import static evosteer.EvolutionSteer.MAX_FOOD_DISTANCE;
import static evosteer.EvolutionSteer.MIN_FOOD_DISTANCE;
import static evosteer.EvolutionSteer.baselineEnergy;
import static processing.core.PApplet.*;

public class EvolutionState {
  public Creature currentCreature;

  public float startingFoodDistance = 0;
  public float foodX = 0;
  public float foodY = 0;
  public float foodZ = 0;
  public float foodAngle = 0;
  public int chomps = 0;
  public int timer = 0;
  public float force;
  public float averageX;
  public float averageY;
  public float averageZ;

  public float energy = 0;
  public float averageNodeNausea = 0;
  public float totalNodeNausea = 0;

  public float camX = 0;
  public float camY = 0;
  public float camZ = 0;
  public float camHA = 0;
  public float camVA = -0.5f;

  public float camZoom = 0.015f;

  public float cumulativeAngularVelocity = 0;

  public int simulationTimer = 0;

  public void resetState(Creature initCreature) {
    currentCreature = initCreature.copyCreature(-1,false,true);
    timer = 0;
    camZoom = 0.01f;
    camX = 0;
    camY = 0;
    camVA = -0.5f;
    camHA = 0.0f;
    simulationTimer = 0;
    energy = baselineEnergy;
    totalNodeNausea = 0;
    averageNodeNausea = 0;
    cumulativeAngularVelocity = 0;
    foodAngle = 0.0f;
    chomps = 0;
    foodX = 0;
    foodY = 0;
    foodZ = 0;
    setFoodLocation();
  }

  public void setFoodLocation(){
    setAverages();
    foodAngle += currentCreature.foodPositions[chomps][0];
    float sinA = sin(foodAngle);
    float cosA = cos(foodAngle);
    float furthestNodeForward = 0;
    for(int i = 0; i < currentCreature.n.size(); i++){
      Node ni = currentCreature.n.get(i);
      float newX = (ni.x-averageX)*cosA-(ni.z-averageZ)*sinA;
      if(newX >= furthestNodeForward){
        furthestNodeForward = newX;
      }
    }
    float d = MIN_FOOD_DISTANCE+(MAX_FOOD_DISTANCE-MIN_FOOD_DISTANCE)*currentCreature.foodPositions[chomps][2];
    foodX = foodX+cos(foodAngle)*(furthestNodeForward+d);
    foodZ = foodZ+sin(foodAngle)*(furthestNodeForward+d);
    foodY = currentCreature.foodPositions[chomps][1];
    startingFoodDistance = getCurrentFoodDistance();
  }

  float getCurrentFoodDistance(){
    float closestDist = 9999;
    for(int i = 0; i < currentCreature.n.size(); i++){
      Node n = currentCreature.n.get(i);
      float distFromFood = dist(n.x,n.y,n.z,foodX,foodY,foodZ)-0.4f;
      if(distFromFood < closestDist){
        closestDist = distFromFood;
      }
    }
    return closestDist;
  }

  public void setAverages() {
    averageX = 0;
    averageY = 0;
    averageZ = 0;
    for (int i = 0; i < currentCreature.n.size(); i++) {
      Node ni = currentCreature.n.get(i);
      averageX += ni.x;
      averageY += ni.y;
      averageZ += ni.z;
    }
    averageX = averageX/currentCreature.n.size();
    averageY = averageY/currentCreature.n.size();
    averageZ = averageZ/currentCreature.n.size();
  }

  public float getFitness(){
    Boolean hasNodeOffGround = false;
    for(int i = 0; i < currentCreature.n.size(); i++){
      if(currentCreature.n.get(i).y <= -0.2001){
        hasNodeOffGround = true;
      }
    }
    if(hasNodeOffGround){
      float withinChomp = max(1.0f-getCurrentFoodDistance()/startingFoodDistance,0);
      return chomps+withinChomp;//cumulativeAngularVelocity/(n.size()-2)/pow(averageNodeNausea,0.3);//   /(2*PI)/(n.size()-2); //dist(0,0,averageX,averageZ)*0.2; // Multiply by 0.2 because a meter is 5 units for some weird reason.
    }else{
      return 0;
    }
  }

  public void setFitness(Creature[] creatureArray, int i){
    creatureArray[i].d = getFitness();
  }

  public void zoomOut() {
    camZoom *= 0.9090909;
    if (camZoom < 0.002) {
      camZoom = 0.002f;
    }
  }

  public void zoomIn() {
    camZoom *= 1.1;
    if (camZoom > 0.1) {
      camZoom = 0.1f;
    }
  }

  public void resetTimer() {
    simulationTimer = 0;
  }

  public void moveCamera(){
    camX += (averageX-camX)*0.2;
    camY += (averageY-camY)*0.2;
    camZ += (averageZ-camZ)*0.2;
  }

  public void cameraAtCreature() {
    camX = averageX;
    camY = averageY;
    camZ = averageZ;
  }

  public void simulateCurrentCreature(){
    currentCreature.simulate();
    averageNodeNausea = totalNodeNausea/currentCreature.n.size();
    simulationTimer++;
    timer++;
  }
}
