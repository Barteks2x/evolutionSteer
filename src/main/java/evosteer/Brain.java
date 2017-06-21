package evosteer;

import processing.core.PApplet;

import java.util.ArrayList;

import static evosteer.EvolutionSteer.AXON_START_MUTABILITY;
import static evosteer.EvolutionSteer.STARTING_AXON_VARIABILITY;
import static evosteer.EvolutionSteer.font;
import static processing.core.PApplet.*;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RADIUS;

public class Brain {
  float[][] neurons;
  AxonArray axons;
  int BRAIN_WIDTH = 0;
  int BRAIN_HEIGHT = 0;

  Brain(int bw, int bh, AxonArray templateAxons, Boolean haveNeurons, Boolean mutate){
    //This is to copy a brain EXACTLY.
    setUpBasics(bw,bh,haveNeurons);
    axons = new AxonArray(BRAIN_WIDTH - 1, BRAIN_HEIGHT, BRAIN_HEIGHT - 1);//[BRAIN_WIDTH-1][BRAIN_HEIGHT][BRAIN_HEIGHT-1];
    if(mutate){
      for(int x = 0; x < BRAIN_WIDTH-1; x++){
        for(int y = 0; y < BRAIN_HEIGHT; y++){
          for(int z = 0; z < BRAIN_HEIGHT-1; z++){
            templateAxons.mutateAxon(x, y, z, axons);
          }
        }
      }
    }else{
      axons = templateAxons;
    }
    axons.setDone();
  }
  Brain(int bw, int bh){
    setUpBasics(bw,bh,false);
    axons = new AxonArray(BRAIN_WIDTH - 1, BRAIN_HEIGHT, BRAIN_HEIGHT - 1);//[BRAIN_WIDTH-1][BRAIN_HEIGHT][BRAIN_HEIGHT-1];
    for(int x = 0; x < BRAIN_WIDTH-1; x++){
      for(int y = 0; y < BRAIN_HEIGHT; y++){
        for(int z = 0; z < BRAIN_HEIGHT-1; z++){
          double startingWeight = 0;
          if(y == BRAIN_HEIGHT-1){
            startingWeight = (Math.random()*2-1)*STARTING_AXON_VARIABILITY;
          }
          axons.set(x, y, z, (float) startingWeight,AXON_START_MUTABILITY);
        }
      }
    }
  }
  void changeBrainStructure(int bw, int bh, int rowInsertionIndex, int rowRemovalIndex){
    setUpBasics(bw,bh,false);
    AxonArray oldAxons = axons;
    axons = new AxonArray(BRAIN_WIDTH - 1, BRAIN_HEIGHT, BRAIN_HEIGHT - 1);
    for(int x = 0; x < BRAIN_WIDTH-1; x++){
      for(int y = 0; y < BRAIN_HEIGHT; y++){
        for(int z = 0; z < BRAIN_HEIGHT-1; z++){
          if(y == rowInsertionIndex || z == rowInsertionIndex){
            double startingWeight = 0;
            if(y == BRAIN_HEIGHT-1 || true){
              startingWeight = (Math.random()*2-1)*STARTING_AXON_VARIABILITY;
            }
            axons.set(x, y, z, (float) startingWeight,AXON_START_MUTABILITY);
          }else{
            int oldY = y;
            int oldZ = z;
            if(rowInsertionIndex >= 0 && y >= rowInsertionIndex) oldY--;
            if(rowInsertionIndex >= 0 && z >= rowInsertionIndex) oldZ--;
            if(rowRemovalIndex >= 0 && y >= rowRemovalIndex) oldY++;
            if(rowRemovalIndex >= 0 && z >= rowRemovalIndex) oldZ++;
            axons.set(x, y, z, oldAxons.weight(x, oldY, oldZ), oldAxons.mutability(x, oldY, oldZ));
          }
        }
      }
    }
  }
  void setUpBasics(int bw, int bh, Boolean haveNeurons){
    BRAIN_WIDTH = bw;
    BRAIN_HEIGHT = bh;
    if(haveNeurons){
      neurons = new float[BRAIN_WIDTH][BRAIN_HEIGHT];
      for(int x = 0; x < BRAIN_WIDTH; x++){
        for(int y = 0; y < BRAIN_HEIGHT; y++){
          if(y == BRAIN_HEIGHT-1){
            neurons[x][y] = 1;
          }else{
            neurons[x][y] = 0;
          }
        }
      }
    }else{
      neurons = null;
    }
  }
  public void useBrain(EvolutionState state, Creature owner){
    //long time = -System.nanoTime();
    ArrayList<Node> n = owner.n;
    ArrayList<Muscle> m = owner.m;
    float foodX = state.foodX;
    float foodY = state.foodY;
    float foodZ = state.foodZ;
    for(int i = 0; i < n.size(); i++){
      Node ni = n.get(i);
      neurons[0][i] = dist(ni.x, ni.y, ni.z, foodX, foodY, foodZ);
    }
    for(int i = 0; i < m.size(); i++){
      Muscle am = m.get(i);
      Node ni1 = n.get(am.c1);
      Node ni2 = n.get(am.c2);
      neurons[0][n.size()+i] = dist(ni1.x, ni1.y, ni1.z, ni2.x, ni2.y, ni2.z)/am.len;
    }
    //AxonArray axons = this.axons;
    for(int x = 1; x < BRAIN_WIDTH; x++){
      for(int y = 0; y < BRAIN_HEIGHT-1; y++){
        float total = 0;
        for(int input = 0; input < BRAIN_HEIGHT; input++){
          total += neurons[x-1][input]*axons.weight(x-1,input,y);
        }
        if(x == BRAIN_WIDTH-1){
          neurons[x][y] = total;
        }else{
          neurons[x][y] = sigmoid(total);
        }
      }
    }
    for(int i = 0; i < m.size(); i++){
      m.get(i).brainOutput = neurons[BRAIN_WIDTH-1][n.size()+i];
    }
    //System.out.println(TimeUnit.NANOSECONDS.toMicros(time + System.nanoTime()));
  }
  public float sigmoid(float input){
    return 0.5f*input/(1.0f+abs(input)) + 0.5f;
  }
  Brain getUsableCopyOfBrain(){
    return new Brain(BRAIN_WIDTH,BRAIN_HEIGHT,axons,true,false);
  }
  Brain copyBrain(){
    return new Brain(BRAIN_WIDTH,BRAIN_HEIGHT,axons,false,false);
  }
  Brain copyMutatedBrain(){
    return new Brain(BRAIN_WIDTH,BRAIN_HEIGHT,axons,false,true);
  }
  public void drawBrain(PApplet p3d, float scaleUp, Creature owner){
    ArrayList<Node> n = owner.n;
    ArrayList<Muscle> m = owner.m;
    final float neuronSize = 0.4f;
    int abw = BRAIN_WIDTH*2-1;
    p3d.noStroke();
    p3d.fill(100);
    p3d.rect(-neuronSize*2*scaleUp,-neuronSize*2*scaleUp,(abw+neuronSize*2)*scaleUp,(BRAIN_HEIGHT+neuronSize*2)*scaleUp);
    p3d.fill(255);
    p3d.rect(-neuronSize*3*scaleUp,-neuronSize*scaleUp,neuronSize*scaleUp,n.size()*scaleUp);
    p3d.fill(0);
    p3d.rect(-neuronSize*3*scaleUp,(n.size()-neuronSize)*scaleUp,neuronSize*scaleUp,m.size()*scaleUp);
    p3d.ellipseMode(RADIUS);
    p3d.strokeWeight(0.5f);
    p3d.textAlign(CENTER);
    p3d.textFont(font,0.58f*scaleUp);
    for(int x = 0; x < BRAIN_WIDTH; x++){
      for(int y = 0; y < BRAIN_HEIGHT; y++){
        p3d.noStroke();
        double val = neurons[x][y];
        p3d.fill(neuronFillColor(p3d, val));
        p3d.ellipse(x*2*scaleUp,y*scaleUp,neuronSize*scaleUp,neuronSize*scaleUp);
        p3d.fill(neuronTextColor(p3d, val));
        p3d.text(nf((float)val,0,1),x*2*scaleUp,(y+(neuronSize*0.6f))*scaleUp);
      }
    }
    for(int x = 0; x < BRAIN_WIDTH-1; x++){
      for(int y = 0; y < BRAIN_HEIGHT; y++){
        for(int z = 0; z < BRAIN_HEIGHT-1; z++){
          drawAxon(p3d, x,y,x+1,z,scaleUp);
        }
      }
    }
  }
  public void drawAxon(PApplet p3d, int x1, int y1, int x2, int y2, float scaleUp){
    p3d.stroke(neuronFillColor(p3d, axons.weight(x1, y1, y2)*neurons[x1][y1]));
    p3d.line(x1*2*scaleUp,y1*scaleUp,x2*2*scaleUp,y2*scaleUp);
  }
  public int neuronFillColor(PApplet p3d, double d){
    if(d >= 0){
      return p3d.color(255,255,255,(float)(d*255));
    }else{
      return p3d.color(1,1,1,abs((float)(d*255)));
    }
  }
  public int neuronTextColor(PApplet p3d, double d){
    if(d >= 0){
      return p3d.color(0,0,0);
    }else{
      return p3d.color(255,255,255);
    }
  }
}
