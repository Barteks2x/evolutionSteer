package evosteer;

import static evosteer.EvolutionSteer.bigMutationChance;
import static evosteer.EvolutionSteer.energyDirection;
import static evosteer.EvolutionSteer.energyUnit;
import static evosteer.EvolutionSteer.scaleToFixBug;
import static evosteer.EvolutionSteer.toMuscleUsable;
import static evosteer.util.Utils.rand;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.max;
import static processing.core.PApplet.min;

import evosteer.util.Utils;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Random;

public class Muscle {
  int c1, c2;
  float len;
  float rigidity;
  private Random random;
  float previousTarget;
  float brainOutput;

  public Muscle(Random random, int tc1, int tc2, float tlen, float trigidity) {
    this.random = random;
    previousTarget = len = tlen;
    c1 = tc1;
    c2 = tc2;
    rigidity = trigidity;
    brainOutput = 1;
  }
  // when state is null, this is only used to get stable configuation
  void applyForce(EvolutionState state, int i, ArrayList<Node> n) {
    float target;
    if(energyDirection == 1 || (state == null || state.energy >= 0.0001)){
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
      float force = min(max(1-(distance/target), -1.7f), 1.7f);
      ni1.vx += normX*force*rigidity/ni1.m;
      ni1.vy += normY*force*rigidity/ni1.m;
      ni1.vz += normZ*force*rigidity/ni1.m;
      ni2.vx -= normX*force*rigidity/ni2.m;
      ni2.vy -= normY*force*rigidity/ni2.m;
      ni2.vz -= normZ*force*rigidity/ni2.m;
      if (state != null) {
        state.energy = max(state.energy + energyDirection * abs(previousTarget - target) * rigidity * energyUnit, 0);
      }
      previousTarget = target;
    }
  }
  Muscle copyMuscle() {
    return new Muscle(random, c1, c2, len, rigidity);
  }
  Muscle modifyMuscle(int nodeNum, float mutability) {
    int newc1 = c1;
    int newc2 = c2;
    if(rand(random, 0,1)<bigMutationChance*mutability){
      newc1 = (int)(rand(random, 0,nodeNum));
    }
    if(rand(random, 0,1)<bigMutationChance*mutability){
      newc2 = (int)(rand(random, 0,nodeNum));
    }
    float newR = min(max(rigidity*(1+ Utils.r(random)*0.9f*mutability),0.015f),0.06f);
    float newLen = min(max(len+Utils.r(random)*mutability,0.4f),1.25f);

    return new Muscle(random, newc1, newc2, newLen, newR);
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
