package evosteer;

import evosteer.util.Utils;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.Random;

import static evosteer.EvolutionSteer.*;

public class Node {
  float x;
  float y;
  float z;
  float vx;
  float vy;
  float vz;
  private Random random;
  float prevX;
  float prevY;
  float prevZ;
  float pvx;
  float pvy;
  float pvz;
  float m;
  float f;
  boolean safeInput;
  float pressure;
  public Node(Random random, float tx, float ty, float tz,
              float tvx, float tvy, float tvz,
              float tm, float tf) {
    this.random = random;
    prevX = x = tx;
    prevY = y = ty;
    prevZ = z = tz;
    pvx = vx = tvx;
    pvy = vy = tvy;
    pvz = vz = tvz;
    m = tm;
    f = tf;
    pressure = 0;
  }
  void applyForces(EvolutionState state) { // state may be null when used for getting stable configuration
    vx *= airFriction;
    vy *= airFriction;
    vz *= airFriction;
    y += vy;
    x += vx;
    z += vz;
    if (state != null) {
      float acc = dist(vx, vy, vz, pvx, pvy, pvz);
      state.totalNodeNausea += acc * acc * nauseaUnit;
    }
    pvx = vx;
    pvy = vy;
    pvz = vz;
  }
  void applyGravity() {
    vy += gravity;
  }
  void pressAgainstGround(float groundY){
    float dif = y-(groundY-m*0.5f);
    pressure += dif*pressureUnit;
    y = (groundY-m*0.5f);
    vy = 0;
    x -= vx*f;
    z -= vz*f;
    if (vx > 0) {
      vx -= f*dif*FRICTION;
      if (vx < 0) {
        vx = 0;
      }
    } else {
      vx += f*dif*FRICTION;
      if (vx > 0) {
        vx = 0;
      }
    }
    if (vz > 0) {
      vz -= f*dif*FRICTION;
      if (vz < 0) {
        vz = 0;
      }
    } else {
      vz += f*dif*FRICTION;
      if (vz > 0) {
        vz = 0;
      }
    }
  }
  void hitWalls(Boolean addToAngular) {
    pressure = 0;
    float dif = y+m*0.5f;
    if (dif >= 0 && haveGround) {
      pressAgainstGround(0);
    }
    prevY = y;
    prevX = x;
  }
  Node copyNode() {
    return (new Node(random, x, y, z, 0, 0, 0, m, f));
  }
  Node modifyNode(float mutability, int nodeNum) {
    float newX = x+Utils.r(random)*0.5f*mutability;
    float newY = y+Utils.r(random)*0.5f*mutability;
    float newZ = z+Utils.r(random)*0.5f*mutability;
    //float newM = m+r()*0.1*mutability;
    //newM = min(max(newM, 0.3), 0.5);
    float newM = 0.4f;
    float newF = min(max(f+Utils.r(random)*0.1f*mutability, 0), 1);
    Node newNode = new Node(random, newX, newY, newZ, 0, 0, 0, newM, newF);
    return newNode;//max(m+r()*0.1,0.2),min(max(f+r()*0.1,0),1)
  }
  void drawNode(PGraphics img) {
    /*int c = APPLET.color(0,0,0);
    if (f <= 0.5) {
      c = colorLerp(APPLET.color(255,255,255),APPLET.color(180,0,255),f*2);
    }else{
      c = colorLerp(APPLET.color(180,0,255),APPLET.color(0,0,0),f*2-1);
    }
    img.fill(c);
    img.noStroke();*/
    //img.lights();
    //img.pushMatrix();
    //img.translate(x*scaleToFixBug, y*scaleToFixBug,z*scaleToFixBug);
    //img.sphere(m*scaleToFixBug*0.5f);

    //img.popMatrix();
    //img.ellipse((ni.x+x)*scaleToFixBug, (ni.y+y)*scaleToFixBug, ni.m*scaleToFixBug, ni.m*scaleToFixBug);
    /*if(ni.f >= 0.5){
      img.fill(255);
    }else{
      img.fill(0);
    }
    img.textAlign(CENTER);
    img.textFont(font, 0.4*ni.m*scaleToFixBug);
    img.text(nf(ni.value,0,2),(ni.x+x)*scaleToFixBug,(ni.y+ni.m*lineY2+y)*scaleToFixBug);
    img.text(operationNames[ni.operation],(ni.x+x)*scaleToFixBug,(ni.y+ni.m*lineY1+y)*scaleToFixBug);*/
  }
  /*int colorLerp(int a, int b, float x){
    return APPLET.color(APPLET.red(a)+(APPLET.red(b)-APPLET.red(a))*x, APPLET.green(a)+(APPLET.green(b)-APPLET.green(a))*x, APPLET.blue(a)+(APPLET.blue(b)-APPLET.blue(a))*x);
  }*/
}
