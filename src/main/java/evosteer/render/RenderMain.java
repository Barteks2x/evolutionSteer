package evosteer.render;

import evosteer.Creature;
import evosteer.EvolutionState;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static evosteer.EvolutionSteer.*;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.nf;

public class RenderMain {
  private final PApplet p3d;
  private final PFont font;
  private final EvolutionState state;
  private final List<Float[]> percentile;
  private final List<Integer[]> speciesCounts;
  final List<Integer> topSpeciesCounts = new ArrayList<>(0);
  final ArrayList<Integer[]> barCounts = new ArrayList<Integer[]>(0);

  public final PGraphics simulationImage;
  private final PGraphics graphImage;
  public final PGraphics screenImage;
  private final PGraphics popUpImage;
  private final PGraphics segBarImage;

  public RenderMain(PApplet applet, PFont font, EvolutionState state,
                    List<Float[]> percentile, List<Integer[]> speciesCounts) {
    p3d = applet;
    this.font = font;
    this.state = state;
    this.percentile = percentile;
    this.speciesCounts = speciesCounts;

    simulationImage = p3d.createGraphics(p3d.width, p3d.height, P3D);
    graphImage = p3d.createGraphics(975, 570);
    screenImage = p3d.createGraphics(p3d.width, p3d.height, P3D);
    popUpImage = p3d.createGraphics(450, 450, P3D);
    segBarImage = p3d.createGraphics(975, 150);

    topSpeciesCounts.add(0);
  }

  public void setup() {
    segBarImage.beginDraw();
    segBarImage.smooth();
    segBarImage.background(220);
    segBarImage.endDraw();
    popUpImage.beginDraw();
    popUpImage.smooth();
    popUpImage.background(220);
    popUpImage.endDraw();
  }

  void drawPosts(PGraphics img) {
    img.noStroke();
    img.textAlign(CENTER);
    img.textFont(font, postFontSize* scaleToFixBug);

    img.fill(0,0,255);
    img.beginShape();
    float s = 0.4f*scaleToFixBug;
    float y = -0.001f*scaleToFixBug;
    img.vertex(0,y,s);
    img.vertex(-s,y,0);
    img.vertex(0,y,-s);
    img.vertex(s,y,0);
    img.endShape(CLOSE);

    p3d.colorMode(HSB,1.0f);
    int c = p3d.color((state.timer%40)/40.0f,1.0f,1.0f);
    img.fill(c);
    img.noStroke();
    img.lights();

    img.pushMatrix();
    img.translate(state.foodX*scaleToFixBug,state.foodY*scaleToFixBug,state.foodZ*scaleToFixBug);
    img.sphere(0.4f*scaleToFixBug*0.5f);
    img.popMatrix();

    img.noLights();

    p3d.colorMode(RGB,255);
    img.fill(0,60,0);
    img.pushMatrix();
    img.translate(state.foodX*scaleToFixBug,0,state.foodZ*scaleToFixBug);
    img.scale(1,0.02f,1);
    img.sphere(0.4f*scaleToFixBug*0.5f);
    img.popMatrix();
  }

  void drawGround(PGraphics img) {
    img.noStroke();
    if (haveGround){
      float groundTileSize = 5.0f;
      int cx = round(state.averageX/5);
      int cz = round(state.averageZ/5);
      for(int x = cx-5; x < cx+5; x++){
        for(int z = cz-5; z < cz+5; z++){
          float lowX = (groundTileSize*x)*scaleToFixBug;
          float highX = (groundTileSize*(x+1))*scaleToFixBug;
          float lowZ = (groundTileSize*z)*scaleToFixBug;
          float highZ = (groundTileSize*(z+1))*scaleToFixBug;
          img.fill(0, 100+((x+z+100)%2)*30, 0);
          img.beginShape();
          img.vertex(lowX, 0, lowZ);
          img.vertex(highX, 0, lowZ);
          img.vertex(highX, 0, highZ);
          img.vertex(lowX, 0, highZ);
          img.endShape(CLOSE);
        }
      }
    }
  }

  void drawArrow(float x, float y, float z, PGraphics img) {
    img.noLights();
    img.pushMatrix();
    img.translate(x*scaleToFixBug,0,z*scaleToFixBug);
    img.rotateY(state.camHA);
    img.rotateX(-state.camVA);
    img.textAlign(CENTER);
    img.textFont(font, postFontSize*scaleToFixBug);
    img.noStroke();
    img.fill(255, 0, 0);
    img.beginShape();
    float dist = 2.7f*scaleToFixBug;
    img.vertex(dist, -3.8f*scaleToFixBug, 0);
    img.vertex(dist, -2.7f*scaleToFixBug, 0);
    img.vertex(-dist, -2.7f*scaleToFixBug, 0);
    img.vertex(-dist, -3.8f*scaleToFixBug, 0);
    img.endShape();
    img.beginShape();
    img.vertex(0, -2.2f*scaleToFixBug);
    img.vertex(-0.5f*scaleToFixBug, -2.7f*scaleToFixBug);
    img.vertex(0.5f*scaleToFixBug, -2.7f*scaleToFixBug);
    img.endShape(CLOSE);
    String fitnessString = nf(state.getFitness(),0,2)+" "+fitnessUnit;
    img.fill(255);
    img.text(fitnessString, 0, -2.91f*scaleToFixBug,0.1f*scaleToFixBug);
    img.popMatrix();
  }

  void drawGraphImage(int gen, int genSelected) {
    p3d.image(graphImage, 50, 180, 650, 380);
    p3d.image(segBarImage, 50, 580, 650, 100);
    if (gen >= 1) {
      p3d.stroke(0, 160, 0, 255);
      p3d.strokeWeight(3);
      float genWidth = 563.333333333f/gen;
      float lineX = 136.66666f+genSelected*genWidth;
      p3d.line(lineX, 180, lineX, 500+180);
      Integer[] s = speciesCounts.get(genSelected);
      p3d.textAlign(RIGHT);
      p3d.textFont(font, 12);
      p3d.noStroke();
      for (int i = 1; i < 101; i++) {
        int c = s[i]-s[i-1];
        if (c >= 25) {
          float y = ((s[i]+s[i-1])/2)/1000.0f*100+573;
          if (i-1 == topSpeciesCounts.get(genSelected)) {
            p3d.stroke(0);
            p3d.strokeWeight(2);
          }
          else {
            p3d.noStroke();
          }
          p3d.fill(255, 255, 255);
          p3d.rect(lineX+3, y, 56, 14);

          p3d.fill(0);
          p3d.text(toRealSpeciesName(i-1)+": "+c, lineX+58, y+11);
        }
      }
      p3d.noStroke();
    }
  }


  public void drawGraph(int gen, int graphWidth, int graphHeight) {
    graphImage.beginDraw();
    graphImage.smooth();
    graphImage.background(220);
    if (gen >= 1) {
      drawLines(gen, 130, (int)(graphHeight*0.05), graphWidth-130, (int)(graphHeight*0.9));
      drawSegBars(gen, 130, 0, graphWidth-130, 150);
    }
    graphImage.endDraw();
  }
  void drawLines(int gen, int x, int y, int graphWidth, int graphHeight) {
    float gh = graphHeight;
    float genWidth = graphWidth/(float)gen;
    float best = extreme(gen, 1);
    float worst = extreme(gen, -1);
    float meterHeight = graphHeight/(best-worst);
    float zero = (best/(best-worst))*gh;
    float unit = setUnit(best, worst);
    graphImage.stroke(150);
    graphImage.strokeWeight(2);
    graphImage.fill(150);
    graphImage.textFont(font, 18);
    graphImage.textAlign(RIGHT);
    for (float i = ceil((worst-(best-worst)/18.0f)/unit)*unit; i < best+(best-worst)/18.0;i+=unit) {
      float lineY = y-i*meterHeight+zero;
      graphImage.line(x, lineY, graphWidth+x, lineY);
      graphImage.text(showUnit(i, unit)+" "+fitnessUnit, x-5, lineY+4);
    }
    graphImage.stroke(0);
    for (int i = 0; i < 29; i++) {
      int k;
      if (i == 28) {
        k = 14;
      }
      else if (i < 14) {
        k = i;
      }
      else {
        k = i+1;
      }
      if (k == 14) {
        graphImage.stroke(255, 0, 0, 255);
        graphImage.strokeWeight(5);
      }
      else {
        p3d.stroke(0);
        if (k == 0 || k == 28 || (k >= 10 && k <= 18)) {
          graphImage.strokeWeight(3);
        }
        else {
          graphImage.strokeWeight(1);
        }
      }
      for (int j = 0; j < gen; j++) {
        graphImage.line(x+j*genWidth, (-percentile.get(j)[k])*meterHeight+zero+y,
                x+(j+1)*genWidth, (-percentile.get(j+1)[k])*meterHeight+zero+y);
      }
    }
  }
  void drawSegBars(int gen, int x, int y, int graphWidth, int graphHeight) {
    segBarImage.beginDraw();
    segBarImage.smooth();
    segBarImage.noStroke();
    segBarImage.colorMode(HSB, 1);
    segBarImage.background(0, 0, 0.5f);
    float genWidth = graphWidth/(float)gen;
    int gensPerBar = floor(gen/500)+1;
    for (int i = 0; i < gen; i+=gensPerBar) {
      int i2 = min(i+gensPerBar, gen);
      float barX1 = x+i*genWidth;
      float barX2 = x+i2*genWidth;
      for (int j = 0; j < 100; j++) {
        segBarImage.fill(getColor(j, false));
        segBarImage.beginShape();
        segBarImage.vertex(barX1, y+speciesCounts.get(i)[j]/1000.0f*graphHeight);
        segBarImage.vertex(barX1, y+speciesCounts.get(i)[j+1]/1000.0f*graphHeight);
        segBarImage.vertex(barX2, y+speciesCounts.get(i2)[j+1]/1000.0f*graphHeight);
        segBarImage.vertex(barX2, y+speciesCounts.get(i2)[j]/1000.0f*graphHeight);
        segBarImage.endShape();
      }
    }
    segBarImage.endDraw();
    p3d.colorMode(RGB, 255);
  }

  void drawHistogram(int genSelected, int x, int y, int hw, int hh) {
    int maxH = 1;
    for (int i = 0; i < barLen; i++) {
      if (barCounts.get(genSelected)[i] > maxH) {
        maxH = barCounts.get(genSelected)[i];
      }
    }
    p3d.fill(200);
    p3d.noStroke();
    p3d.rect(x, y, hw, hh);
    p3d.fill(0, 0, 0);
    float barW = (float)hw/barLen;
    float multiplier = (float)hh/maxH*0.9f;
    p3d.textAlign(LEFT);
    p3d.textFont(font, 16);
    p3d.stroke(128);
    p3d.strokeWeight(2);
    int unit = 100;
    if (maxH < 300) unit = 50;
    if (maxH < 100) unit = 20;
    if (maxH < 50) unit = 10;
    for (int i = 0; i < hh/multiplier; i += unit) {
      float theY = y+hh-i*multiplier;
      p3d.line(x, theY, 0, x+hw, theY, 0);
      if (i == 0) theY -= 5;
      p3d.text(i, x+hw+5, theY+7, 0);
    }
    p3d.textAlign(CENTER);
    for (int i = minBar; i <= maxBar; i += 10) {
      if (i == 0) {
        p3d.stroke(0, 0, 255);
      }
      else {
        p3d.stroke(128);
      }
      float theX = x+(i-minBar)*barW;
      p3d.text(nf((float)i/histBarsPerMeter, 0, 1), theX, y+hh+14, 0);
      p3d.line(theX, y, 0, theX, y+hh, 0);
    }
    p3d.noStroke();
    for (int i = 0; i < barLen; i++) {
      float h = min(barCounts.get(genSelected)[i]*multiplier, hh);
      if (i+minBar == floor(percentile.get(min(genSelected, percentile.size()-1))[14]*histBarsPerMeter)) {
        p3d.fill(255, 0, 0);
      }
      else {
        p3d.fill(0, 0, 0);
      }
      p3d.rect(x+i*barW, y+hh-h, barW, h);
    }
  }


  public void drawStatusWindow(Creature cj, int statusWindow, int x, int y, int px, int py) {

    int rank = (statusWindow+1);

    p3d.stroke(abs(overallTimer%30-15)*17);
    p3d.strokeWeight(3);
    p3d.noFill();
    if (statusWindow >= 0) {
      p3d.rect(x*30+40, y*25+17, 30, 25);
    } else {

      p3d.rect(x, y, 140, 140);
      int[] ranks = {
              1000, 500, 1
      };
      rank = ranks[statusWindow+3];
    }
    p3d.noStroke();
    p3d.fill(255);
    p3d.rect(px-60, py, 120, 52);
    p3d.fill(0);
    p3d.textFont(font, 12);
    p3d.textAlign(CENTER);
    p3d.text("#"+rank, px, py+12);
    p3d.text("ID: "+cj.id, px, py+24);
    p3d.text("Fitness: "+nf(cj.d, 0, 3), px, py+36);
    p3d.colorMode(HSB, 1);
    int sp = (cj.n.size()%10)*10+(cj.m.size()%10);
    p3d.fill(getColor(sp, true));
    p3d.text("Species: S"+(cj.n.size()%10)+""+(cj.m.size()%10), px, py+48);
    p3d.colorMode(RGB, 255);
  }

  public void drawMiniSimulation(Creature cj, int px, int py) {
    int py2 = py-175;
    if (py >= 360) {
      py2 -= 190;
    }else {
      py2 += 238;
    }
    int px2 = min(max(px-90, 10), 900);
    drawpopUpImage();
    p3d.pushMatrix();
    p3d.translate(0,0,1);
    p3d.image(popUpImage, px2, py2, 360, 360);
    p3d.popMatrix();
    drawBrain(px2-130, py2, 1,5, cj);
    drawStats(px2+355, py2+239, 1, 0.45f);

    state.simulateCurrentCreature();
  }
  void drawpopUpImage() {
    state.setAverages();
    state.moveCamera();
    popUpImage.beginDraw();
    popUpImage.smooth();

    float camDist = (450/2.0f) / tan(PI*30.0f / 180.0f);
    popUpImage.pushMatrix();

    popUpImage.camera(state.camX/state.camZoom+camDist*sin(state.camHA)*cos(state.camVA),
            state.camY/state.camZoom+camDist*sin(state.camVA), state.camZ/state.camZoom+camDist*cos(state.camHA)*cos(state.camVA),
            state.camX/state.camZoom, state.camY/state.camZoom, state.camZ/state.camZoom, 0, 1, 0);

    popUpImage.scale(1.0f/state.camZoom/scaleToFixBug);

    if (state.simulationTimer < 900) {
      popUpImage.background(120, 200, 255);
    } else {
      popUpImage.background(60, 100, 128);
    }
    drawPosts(popUpImage);
    drawGround(popUpImage);
    state.currentCreature.drawCreature(popUpImage,false);
    drawArrow(state.averageX,state.averageY,state.averageZ,popUpImage);
    popUpImage.noStroke();
    popUpImage.endDraw();
    popUpImage.popMatrix();
  }

  public void drawScreenImage(Creature[] c, List<Creature> c2, int[] creaturesInPosition, int gen, int stage, int gridBGColor) {
    screenImage.beginDraw();
    screenImage.pushMatrix();
    screenImage.scale(10.0f*windowSizeMultiplier/scaleToFixBug);
    screenImage.smooth();
    screenImage.background(gridBGColor);
    screenImage.noStroke();
    for (int j = 0; j < 1000; j++) {
      Creature cj = c2.get(j);
      if (stage == 3) cj = c[cj.id-(gen*1000)-1001];
      int j2 = j;
      if (stage == 0) {
        j2 = cj.id-(gen*1000)-1;
        creaturesInPosition[j2] = j;
      }
      int x = j2%40;
      int y = floor(j2/40);
      if (stage >= 1) y++;
      screenImage.pushMatrix();
      screenImage.translate((x*3+5.5f)*scaleToFixBug, (y*2.5f+3)*scaleToFixBug, 0);
      cj.drawCreature(screenImage,true);
      screenImage.popMatrix();
    }
    state.timer = 0;
    screenImage.popMatrix();
    screenImage.noLights();

    screenImage.pushMatrix();
    screenImage.scale(windowSizeMultiplier); // Arbitrary, do not change.

    screenImage.textAlign(CENTER);
    screenImage.textFont(font, 24);
    screenImage.fill(100, 100, 200);
    screenImage.noStroke();
    if (stage == 0) {
      screenImage.rect(900, 664, 260, 40);
      screenImage.fill(0);
      screenImage.text("All 1,000 creatures have been tested.  Now let's sort them!", windowWidth/2-200, 690);
      screenImage.text("Sort", windowWidth-250, 690);
    } else if (stage == 1) {
      screenImage.rect(900, 670, 260, 40);
      screenImage.fill(0);
      screenImage.text("Fastest creatures at the top!", windowWidth/2, 30);
      screenImage.text("Slowest creatures at the bottom. (Going backward = slow)", windowWidth/2-200, 700);
      screenImage.text("Kill 500", windowWidth-250, 700);
    } else if (stage == 2) {
      screenImage.rect(1050, 670, 160, 40);
      screenImage.fill(0);
      screenImage.text("Faster creatures are more likely to survive because they can outrun their predators.  Slow creatures get eaten.", windowWidth/2, 30);
      screenImage.text("Because of random chance, a few fast ones get eaten, while a few slow ones survive.", windowWidth/2-130, 700);
      screenImage.text("Reproduce", windowWidth-150, 700);
      for (int j = 0; j < 1000; j++) {
        Creature cj = c2.get(j);
        int x = j%40;
        int y = floor(j/40)+1;
        if (!cj.alive) {
          screenImage.fill(0);
          screenImage.beginShape();
          screenImage.vertex(x*30+40, y*25+17,0.01f);
          screenImage.vertex(x*30+70, y*25+17,0.01f);
          screenImage.vertex(x*30+70, y*25+42,0.01f);
          screenImage.vertex(x*30+40, y*25+42,0.01f);
          screenImage.endShape();
        }
      }
    } else if (stage == 3) {
      screenImage.rect(1050, 670, 160, 40);
      screenImage.fill(0);
      screenImage.text("These are the 1000 creatures of generation #"+(gen+2)+".", windowWidth/2, 30);
      screenImage.text("What perils will they face?  Find out next time!", windowWidth/2-130, 700);
      screenImage.text("Back", windowWidth-150, 700);
    }
    screenImage.popMatrix();
    screenImage.endDraw();
  }

  void drawStats(float x, float y, float z, float size){
    p3d.textAlign(RIGHT);
    p3d.textFont(font, 32);
    p3d.fill(0);
    p3d.pushMatrix();
    p3d.translate(x,y,z);
    p3d.scale(size);
    p3d.text(toRealName(state.currentCreature.name), 0, 32);
    p3d.text("Creature ID: "+state.currentCreature.id, 0, 64);
    p3d.text("Time: "+nf(state.timer/60.0f,0,2)+" / 15 sec.", 0, 96);
    p3d.text("Playback Speed: x"+max(1, simulationSpeed), 0, 128);
    String extraWord = "used";
    if(energyDirection == -1){
      extraWord = "left";
    }
    p3d.text("X: "+nf(state.averageX/5.0f,0,2)+"", 0, 160);
    p3d.text("Y: "+nf(-state.averageY/5.0f,0,2)+"", 0, 192);
    p3d.text("Z: "+nf(-state.averageZ/5.0f,0,2)+"", 0, 224);

    p3d.popMatrix();
  }

  void drawBrain(float x, float y, float z, float size, Creature c){
    p3d.pushMatrix();
    p3d.translate(x,y,z);
    p3d.scale(size);
    state.currentCreature.brain.drawBrain(p3d, size,state.currentCreature);
    p3d.popMatrix();
  }
  void drawSkipButton(){
    p3d.fill(0);
    p3d.rect(0,windowHeight-40,90,40);
    p3d.fill(255);
    p3d.textAlign(CENTER);
    p3d.textFont(font, 32);
    p3d.text("SKIP",45,windowHeight-8);
  }
  void drawOtherButtons(){
    p3d.fill(0);
    p3d.rect(120,windowHeight-40,240,40);
    p3d.fill(255);
    p3d.textAlign(CENTER);
    p3d.textFont(font, 32);
    p3d.text("PB simulationSpeed: x"+ simulationSpeed,240,windowHeight-8);
    p3d.fill(0);
    p3d.rect(windowWidth-120,windowHeight-40,120,40);
    p3d.fill(255);
    p3d.textAlign(CENTER);
    p3d.textFont(font, 32);
    p3d.text("FINISH",windowWidth-60,windowHeight-8);
  }

  String toRealName(int[] n){
    String[] parts = patronData[n[0]].split(",");
    if(parts[1].length() == 0){
      return parts[0]+"'s "+rankify(n[1]+1)+" creature";
    }else{
      return parts[0]+" "+parts[1]+"'s "+rankify(n[1]+1)+" creature";
    }
  }
  String toRealSpeciesName(int n){
    String[] parts = patronData[n].split(",");
    if(parts[1].length() == 0){
      return parts[0];
    }else{
      return parts[0]+" "+parts[1];
    }
  }
  String showUnit(float i, float unit) {
    if (unit < 1) {
      return nf(i, 0, 2)+"";
    }
    else {
      return (int)(i)+"";
    }
  }

  float setUnit(float best, float worst) {
    float unit2 = 3*log(best-worst)/log(10)-2;
    if ((unit2+90)%3 < 1) {
      return pow(10, floor(unit2/3));
    } else if ((unit2+90)%3 < 2) {
      return pow(10, floor((unit2-1)/3))*2;
    } else {
      return pow(10, floor((unit2-2)/3))*5;
    }
  }

  float extreme(int gen, float sign) {
    float record = -sign;
    for (int i = 0; i < gen; i++) {
      float toTest = percentile.get(i+1)[(int)(14-sign*14)];
      if (toTest*sign > record*sign) {
        record = toTest;
      }
    }
    return record;
  }

  int getColor(int i, boolean adjust) {
    p3d.colorMode(HSB, 1.0f);
    float col = (i*1.618034f)%1;
    if (i == 46) {
      col = 0.083333f;
    }
    float light = 1.0f;
    if (abs(col-0.333f) <= 0.18 && adjust) {
      light = 0.7f;
    }
    int c = p3d.color(col, 1.0f, light);
    p3d.colorMode(RGB, 255);
    return c;
  }

  int rInt() {
    return (int)(p3d.random(-0.01f, 1.01f));
  }

  String rankify(int s){
    if(s >= 11 && s <= 19){
      return s+"th";
    }else if(s%10 == 1){
      return s+"st";
    }else if(s%10 == 2){
      return s+"nd";
    }else if(s%10 == 3){
      return s+"rd";
    }else{
      return s+"th";
    }
  }

  public void renderScreenImage() {
    p3d.image(screenImage, 0, 0, 1280, 720);
  }
}
