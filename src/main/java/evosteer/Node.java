package evosteer;

import evosteer.util.Utils;
import processing.core.PApplet;
import processing.core.PGraphics;
import static evosteer.EvolutionSteer.*;

public class Node {
  private EvolutionState state;

  float x;
  float y;
  float z;
  float vx;
  float vy;
  float vz;
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
  public Node(EvolutionState state, float tx, float ty, float tz,
  float tvx, float tvy, float tvz,
  float tm, float tf) {
    this.state = state;
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
  void applyForces() {
    vx *= airFriction;
    vy *= airFriction;
    vz *= airFriction;
    y += vy;
    x += vx;
    z += vz;
    float acc = dist(vx,vy,vz,pvx,pvy,pvz);
    state.totalNodeNausea += acc*acc*nauseaUnit;
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
    /*for (int i = 0; i < rects.size(); i++) {
      Rectangle r = rects.get(i);
      boolean flip = false;
      float px, py;
      if (abs(x-(r.x1+r.x2)/2) <= (r.x2-r.x1+m)/2 && abs(y-(r.y1+r.y2)/2) <= (r.y2-r.y1+m)/2) {
        if (x >= r.x1 && x < r.x2 && y >= r.y1 && y < r.y2) {
          float d1 = x-r.x1;
          float d2 = r.x2-x;
          float d3 = y-r.y1;
          float d4 = r.y2-y;
          if (d1 < d2 && d1 < d3 && d1 < d4) {
            px = r.x1;
            py = y;
          }else if (d2 < d3 && d2 < d4) {
            px = r.x2;
            py = y;
          }else if (d3 < d4) {
            px = x;
            py = r.y1;
          } else {
            px = x;
            py = r.y2;
          }
          flip = true;
        } else {
          if (x < r.x1) {
            px = r.x1;
          }else if (x < r.x2) {
            px = x;
          }else {
            px = r.x2;
          }
          if (y < r.y1) {
            py = r.y1;
          }else if (y < r.y2) {
            py = y;
          }else {
            py = r.y2;
          }
        }
        float distance = dist(x, y, px, py);
        float rad = m/2;
        float wallAngle = atan2(py-y, px-x);
        if (flip) {
          wallAngle += PI;
        }
        if (distance < rad || flip) {
          dif = rad-distance;
          pressure += dif*pressureUnit;
          float multi = rad/distance;
          if (flip) {
            multi = -multi;
          }
          x = (x-px)*multi+px;
          y = (y-py)*multi+py;
          float veloAngle = atan2(vy, vx);
          float veloMag = dist(0, 0, vx, vy);
          float relAngle = veloAngle-wallAngle;
          float relY = sin(relAngle)*veloMag*dif*FRICTION;
          vx = -sin(relAngle)*relY;
          vy = cos(relAngle)*relY;
        }
      }
    }*/
    prevY = y;
    prevX = x;
  }
  Node copyNode() {
    return (new Node(state, x, y, z, 0, 0, 0, m, f));
  }
  Node modifyNode(float mutability, int nodeNum) {
    float newX = x+APPLET.r()*0.5f*mutability;
    float newY = y+APPLET.r()*0.5f*mutability;
    float newZ = z+APPLET.r()*0.5f*mutability;
    //float newM = m+r()*0.1*mutability;
    //newM = min(max(newM, 0.3), 0.5);
    float newM = 0.4f;
    float newF = min(max(f+APPLET.r()*0.1f*mutability, 0), 1);
    Node newNode = new Node(state, newX, newY, newZ, 0, 0, 0, newM, newF);
    return newNode;//max(m+r()*0.1,0.2),min(max(f+r()*0.1,0),1)
  }
  void drawNode(PGraphics img) {
    int c = APPLET.color(0,0,0);
    if (f <= 0.5) {
      c = colorLerp(APPLET.color(255,255,255),APPLET.color(180,0,255),f*2);
    }else{
      c = colorLerp(APPLET.color(180,0,255),APPLET.color(0,0,0),f*2-1);
    }
    img.fill(c);
    img.noStroke();
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
  int colorLerp(int a, int b, float x){
    return APPLET.color(APPLET.red(a)+(APPLET.red(b)-APPLET.red(a))*x, APPLET.green(a)+(APPLET.green(b)-APPLET.green(a))*x, APPLET.blue(a)+(APPLET.blue(b)-APPLET.blue(a))*x);
  }
}
