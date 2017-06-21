package evosteer;

import static evosteer.EvolutionSteer.APPLET;
import static evosteer.EvolutionSteer.bigMutationChance;
import static evosteer.EvolutionSteer.energyDirection;
import static evosteer.EvolutionSteer.energyUnit;
import static evosteer.EvolutionSteer.scaleToFixBug;
import static evosteer.EvolutionSteer.toMuscleUsable;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.max;
import static processing.core.PApplet.min;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

public class Muscle {
  int c1, c2;
  float len;
  float rigidity;
  float previousTarget;
  float brainOutput;
  private EvolutionState state;
  public Muscle(EvolutionState state, int tc1, int tc2, float tlen, float trigidity) {
    this.state = state;
    previousTarget = len = tlen;
    c1 = tc1;
    c2 = tc2;
    rigidity = trigidity;
    brainOutput = 1;
  }
  void applyForce(int i, ArrayList<Node> n) {
    float target = previousTarget;
    if(energyDirection == 1 || state.energy >= 0.0001){
      target = len*toMuscleUsable(brainOutput);
    }else{
      target = len;
    }
    Node ni1 = n.get(c1);
    Node ni2 = n.get(c2);
    float distance = dist(ni1.x, ni1.y, ni1.z, ni2.x, ni2.y, ni2.z);
    if(distance >= 0.0001){
      float normX = (ni1.x-ni2.x)/distance;
      float normY = (ni1.y-ni2.y)/distance;
      float normZ = (ni1.z-ni2.z)/distance;
      state.force = min(max(1-(distance/target), -1.7f), 1.7f);
      ni1.vx += normX*state.force*rigidity/ni1.m;
      ni1.vy += normY*state.force*rigidity/ni1.m;
      ni1.vz += normZ*state.force*rigidity/ni1.m;
      ni2.vx -= normX*state.force*rigidity/ni2.m;
      ni2.vy -= normY*state.force*rigidity/ni2.m;
      ni2.vz -= normZ*state.force*rigidity/ni2.m;
      state.energy = max(state.energy+energyDirection*abs(previousTarget-target)*rigidity*energyUnit,0);
      previousTarget = target;
    }
  }
  Muscle copyMuscle() {
    return new Muscle(state, c1, c2, len, rigidity);
  }
  Muscle modifyMuscle(int nodeNum, float mutability) {
    int newc1 = c1;
    int newc2 = c2;
    if(APPLET.random(0,1)<bigMutationChance*mutability){
      newc1 = (int)(APPLET.random(0,nodeNum));
    }
    if(APPLET.random(0,1)<bigMutationChance*mutability){
      newc2 = (int)(APPLET.random(0,nodeNum));
    }
    float newR = min(max(rigidity*(1+APPLET.r()*0.9f*mutability),0.015f),0.06f);
    float newLen = min(max(len+APPLET.r()*mutability,0.4f),1.25f);

    return new Muscle(state, newc1, newc2, newLen, newR);
  }
  void drawMuscle(ArrayList<Node> n, PGraphics img) {
    Node ni1 = n.get(c1);
    Node ni2 = n.get(c2);
    float w = toMuscleUsable(brainOutput)*0.15f;
    img.strokeWeight(w*scaleToFixBug);
    float brownness = rigidity*13;
    img.stroke(255-180*brownness, 255-210*brownness, 255-255*brownness, 255);
    img.line(ni1.x*scaleToFixBug, ni1.y*scaleToFixBug, 
    ni1.z*scaleToFixBug, 
    ni2.x*scaleToFixBug, ni2.y*scaleToFixBug,
    ni2.z*scaleToFixBug);
  }
}
