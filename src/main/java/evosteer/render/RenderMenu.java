package evosteer.render;

import evosteer.Creature;
import evosteer.EvolutionState;
import evosteer.Muscle;
import evosteer.Node;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

import static evosteer.EvolutionSteer.*;
import static evosteer.util.Utils.inter;
import static evosteer.util.Utils.quickSort;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;

public class RenderMenu {
  private int fontSize = 0;
  private int[] fontSizes = {
          50, 36, 25, 20, 16, 14, 11, 9
  };
  private int creatures = 0;
  private final PApplet p3d;
  private final RenderMain renderMain;
  private final PFont font;
  private final EvolutionState state;

  // TODO: make it non-global
  private final List<Float[]> percentile;

  private final List<Integer[]> speciesCounts;

  public final int gridBGColor;

  public RenderMenu(PApplet applet, RenderMain renderMain, PFont font, EvolutionState state,
                    List<Float[]> percentile, List<Integer[]> speciesCounts) {
    this.p3d = applet;
    this.renderMain = renderMain;
    this.font = font;
    this.state = state;

    this.percentile = percentile;
    this.speciesCounts = speciesCounts;

    this.gridBGColor = p3d.color(220, 253, 102, 255);

    Float[] beginPercentile = new Float[29];
    Integer[] beginBar = new Integer[barLen];
    Integer[] beginSpecies = new Integer[101];
    for (int i = 0; i < 29; i++) {
      beginPercentile[i] = 0.0f;
    }
    for (int i = 0; i < barLen; i++) {
      beginBar[i] = 0;
    }
    for (int i = 0; i < 101; i++) {
      beginSpecies[i] = 500;
    }

    percentile.add(beginPercentile);
    renderMain.barCounts.add(beginBar);
    speciesCounts.add(beginSpecies);
  }

  public void renderWelcomeMenu() {
    p3d.background(255);
    p3d.fill(100, 200, 100);
    p3d.noStroke();
    p3d. rect(windowWidth/2-200, 300, 400, 200);
    p3d.fill(0);
    p3d.text("EVOLUTION!", windowWidth/2, 200);
    p3d.text("START", windowWidth/2, 430);
  }

  public void renderMainMenu(int generation, int genSelected, int gensToDo) {
    p3d.noStroke();
    p3d.fill(0);
    p3d.background(255, 200, 130);
    p3d.textFont(font, 32);
    p3d.textAlign(LEFT);
    p3d.textFont(font, 96);
    p3d.text("GEN "+max(genSelected, 0), 20, 100);
    p3d.textFont(font, 28);
    if (generation == -1) {
      p3d.fill(100, 200, 100);
      p3d.rect(20, 250, 200, 100);
      p3d.fill(0);
      p3d.text("Since there are no creatures yet, create 1000 creatures!", 20, 160);
      p3d.text("They will be randomly created, and also very simple.", 20, 200);
      p3d.text("CREATE", 56, 312);
    } else {
      p3d.fill(100, 200, 100);
      p3d.rect(760, 20, 460, 40);
      p3d. rect(760, 70, 460, 40);
      p3d.rect(760, 120, 230, 40);
      if (gensToDo >= 2) {
        p3d.fill(128, 255, 128);
      } else {
        p3d.fill(70, 140, 70);
      }
      p3d.rect(990, 120, 230, 40);
      p3d.fill(0);
      //text("Survivor Bias: "+percentify(getSB(genSelected)), 437, 50);
      p3d.text("Curve: ±"+nf(foodAngleChange/(2*PI)*360,0,2)+" degrees", 420, 50);
      p3d.text("Do 1 step-by-step generation.", 770, 50);
      p3d.text("Do 1 quick generation.", 770, 100);
      p3d.text("Do 1 gen ASAP.", 770, 150);
      p3d.text("Do gens ALAP.", 1000, 150);
      p3d.text("Median "+fitnessName, 50, 160);
      p3d.textAlign(CENTER);
      p3d.textAlign(RIGHT);
      p3d.text((round(percentile.get(min(genSelected, percentile.size()-1))[14]*1000))/1000+" "+fitnessUnit, 700, 160);
      renderMain.drawHistogram(genSelected, 760, 410, 460, 280);
      renderMain.drawGraphImage(generation, genSelected);
    }
  }

  public void renderRandom1000CreaturesMenu() {
    creatures = 0;
    for (int y = 0; y < 25; y++) {
      for (int x = 0; x < 40; x++) {
        int nodeNum = (int)(p3d.random(4, 8));
        int muscleNum = (int)(p3d.random(nodeNum, nodeNum*3));
        ArrayList<Node> n = new ArrayList<Node>(nodeNum);
        ArrayList<Muscle> m = new ArrayList<Muscle>(muscleNum);
        for (int i = 0; i < nodeNum; i++) {
          n.add(new Node(state, p3d.random(-1, 1), p3d.random(-1, 1), p3d.random(-1, 1),
                  0, 0, 0, 0.4f, p3d.random(0, 1))); //replaced all nodes' sizes with 0.4, used to be random(0.1,1), random(0,1)
        }
        for (int i = 0; i < muscleNum; i++) {
          int tc1 = 0;
          int tc2 = 0;
          if (i < nodeNum-1) {
            tc1 = i;
            tc2 = i+1;
          } else {
            tc1 = (int)(p3d.random(0, nodeNum));
            tc2 = tc1;
            while (tc2 == tc1) {
              tc2 = (int)(p3d.random(0, nodeNum));
            }
          }
          float s = 0.8f;
          if (i >= 10) {
            s *= 1.414;
          }
          float len = p3d.random(0.5f,1.5f);
          m.add(new Muscle(state, tc1, tc2, len, p3d.random(0.015f, 0.06f)));
        }
        float heartbeat = p3d.random(40, 80);
        creatureArray[y*40+x] = new Creature(state, null, y*40+x+1, new ArrayList<Node>(n), new ArrayList<Muscle>(m), 0, true, heartbeat, 1.0f, null, null);
        creatureArray[y*40+x].checkForOverlap();
        creatureArray[y*40+x].checkForLoneNodes();
        creatureArray[y*40+x].toStableConfiguration();
        creatureArray[y*40+x].moveToCenter();
      }
    }
    creatures = 0;

    PGraphics screenImage = renderMain.screenImage;

    screenImage.beginDraw();
    screenImage.background(gridBGColor);
    screenImage.scale(windowSizeMultiplier);
    screenImage.pushMatrix();
    screenImage.scale(10.0f/scaleToFixBug);
    for (int y = 0; y < 25; y++) {
      for (int x = 0; x < 40; x++) {
        screenImage.pushMatrix();
        screenImage.translate((x*3+5.5f)*scaleToFixBug, (y*2.5f+3)*scaleToFixBug, 0);
        creatureArray[y*40+x].drawCreature(screenImage,true);
        screenImage.popMatrix();
      }
    }
    screenImage.noLights();
    screenImage.popMatrix();
    screenImage.noStroke();
    screenImage.fill(100, 100, 200);
    screenImage.rect(900, 664, 260, 40);
    screenImage.fill(0);
    screenImage.textAlign(CENTER);
    screenImage.textFont(font, 24);
    screenImage.text("Here are your 1000 randomly generated creatures!!!", windowWidth/2-200, 690);
    screenImage.text("Back", windowWidth-250, 690);
    screenImage.endDraw();
  }

  public void renderMenu3() {
    p3d.background(0,0,255);
    p3d.image(renderMain.screenImage, 0, 0, 1280, 720);
  }

  public void renderMenu5(boolean isSimulating, boolean justFinished) {
    if (isSimulating) {

      float camDist = (p3d.height/2.0f) / tan(PI*30.0f / 180.0f);

      PGraphics simulationImage = renderMain.simulationImage;
      simulationImage.beginDraw();
      simulationImage.background(120, 200, 255);
      simulationImage.pushMatrix();
      simulationImage.camera(state.camX/state.camZoom+camDist*sin(state.camHA)*cos(state.camVA),
              state.camY/state.camZoom+camDist*sin(state.camVA), state.camZ/state.camZoom+camDist*cos(state.camHA)*cos(state.camVA),
              state.camX/state.camZoom, state.camY/state.camZoom, state.camZ/state.camZoom, 0, 1, 0);

      simulationImage.scale(1.0f/state.camZoom/scaleToFixBug);

      renderMain.drawPosts(simulationImage);
      renderMain.drawGround(simulationImage);
      state.currentCreature.drawCreature(simulationImage,false);
      renderMain.drawArrow(state.averageX,state.averageY,state.averageZ,simulationImage);
      simulationImage.popMatrix();
      simulationImage.endDraw();
      p3d.image(simulationImage,0,0,p3d.width/windowSizeMultiplier,
              p3d.height/windowSizeMultiplier);
      renderMain.drawBrain(40,20,0,5,state.currentCreature);
      renderMain.drawStats(windowWidth-10,0,0,0.7f);
      renderMain.drawSkipButton();
      renderMain.drawOtherButtons();
    }
    if (justFinished) {
      if (simulationSpeed < 30) {
        p3d.noStroke();
        p3d.fill(0, 0, 0, 130);
        p3d.rect(0, 0, windowWidth, windowHeight);
        p3d.fill(0, 0, 0, 255);
        p3d.rect(windowWidth/2-500, 200, 1000, 240);
        p3d.fill(255, 0, 0);
        p3d.textAlign(CENTER);
        p3d.textFont(font, 96);
        p3d.text("Creature's "+fitnessName+":", windowWidth/2, 300);
        p3d.text(nf(state.getFitness(),0,2) + " "+fitnessUnit, windowWidth/2, 400);
      } else {
        state.timer = 1020;
      }
    }
  }

  public void renderMenu6(int generation) {
    //sort
    creatureList = new ArrayList<Creature>(0);
    for(int i = 0; i < 1000; i++){
      creatureList.add(creatureArray[i]);
    }
    creatureList = quickSort(creatureList);
    percentile.add(new Float[29]);
    for (int i = 0; i < 29; i++) {
      percentile.get(generation+1)[i] = creatureList.get(p[i]).d;
    }
    creatureDatabase.add(creatureList.get(999).copyCreature(-1,false,false));
    creatureDatabase.add(creatureList.get(499).copyCreature(-1,false,false));
    creatureDatabase.add(creatureList.get(0).copyCreature(-1,false,false));

    Integer[] beginBar = new Integer[barLen];
    for (int i = 0; i < barLen; i++) {
      beginBar[i] = 0;
    }
    renderMain.barCounts.add(beginBar);
    Integer[] beginSpecies = new Integer[101];
    for (int i = 0; i < 101; i++) {
      beginSpecies[i] = 0;
    }
    for (int i = 0; i < 1000; i++) {
      int bar = floor(creatureList.get(i).d*histBarsPerMeter-minBar);
      if (bar >= 0 && bar < barLen) {
        renderMain.barCounts.get(generation+1)[bar]++;
      }
      int species = creatureList.get(i).name[0];//(creatureList.get(i).n.size()%10)*10+creatureList.get(i).m.size()%10;
      beginSpecies[species]++;
    }
    speciesCounts.add(new Integer[101]);
    speciesCounts.get(generation+1)[0] = 0;
    int cum = 0;
    int record = 0;
    int holder = 0;
    for (int i = 0; i < 100; i++) {
      cum += beginSpecies[i];
      speciesCounts.get(generation+1)[i+1] = cum;
      if (beginSpecies[i] > record) {
        record = beginSpecies[i];
        holder = i;
      }
    }
    renderMain.topSpeciesCounts.add(holder);
  }

  public void renderAllCreaturesMenu(int generation) {
    state.timer = 100000; // DOY IM IMPATIENT

    PGraphics screenImage = renderMain.screenImage;

    //cool sorting animation
    screenImage.beginDraw();
    screenImage.background(gridBGColor);
    screenImage.pushMatrix();
    screenImage.scale(10.0f/scaleToFixBug*windowSizeMultiplier);
    float transition = 0.5f-0.5f*cos(min((state.timer)/60f, PI));
    for (int j = 0; j < 1000; j++) {
      Creature cj = creatureList.get(j);
      int j2 = cj.id-(generation*1000)-1;
      int x1 = j2%40;
      int y1 = floor(j2/40);
      int x2 = j%40;
      int y2 = floor(j/40)+1;
      float x3 = inter(x1, x2, transition);
      float y3 = inter(y1, y2, transition);
      screenImage.translate((x3*3+5.5f)*scaleToFixBug, (y3*2.5f+4)*scaleToFixBug, 0);
      cj.drawCreature(screenImage,true);
    }
    screenImage.popMatrix();
    if (stepbystepslow) {
      state.timer+=5;
    }else{
      state.timer+=20;
    }
    screenImage.endDraw();
    p3d.image(screenImage, 0, 0, 1280, 720);
    renderMain.drawSkipButton();
  }

  public void drawMenu1(int gen, final int genSelected) {
    float mX = p3d.mouseX/windowSizeMultiplier;
    float mY = p3d.mouseY/windowSizeMultiplier;
    p3d.noStroke();
    if (gen >= 1) {
      p3d.textAlign(CENTER);

      if (drag) sliderX = min(max(sliderX+(mX-25-sliderX)*0.2f, 760), 1170);
      p3d.fill(100);
      p3d.rect(760, 340, 460, 50);
      p3d.fill(220);
      p3d.rect(sliderX, 340, 50, 50);
      int fs = 0;
      if (genSelected >= 1) {
        fs = floor(log(genSelected)/log(10));
      }
      fontSize = fontSizes[fs];
      p3d.textFont(font, fontSize);
      p3d.fill(0);
      p3d.text(genSelected, sliderX+25, 366+fontSize*0.3333f);
    }
    if (genSelected >= 1) {
      PGraphics simulationImage = renderMain.simulationImage;

      simulationImage.beginDraw();
      simulationImage.clear();
      simulationImage.endDraw();
      for (int k = 0; k < 3; k++) {
        p3d.fill(220);
        p3d.rect(760+k*160, 180, 140, 140);
        simulationImage.beginDraw();
        simulationImage.pushMatrix();
        simulationImage.translate(830+160*k, 260,0);
        simulationImage.scale(60.0f/scaleToFixBug);
        creatureDatabase.get((genSelected-1)*3+k).drawCreature(simulationImage,true);
        simulationImage.popMatrix();
        simulationImage.endDraw();
      }
      p3d.image(simulationImage,0,0,p3d.width,p3d.height);

      p3d.textAlign(CENTER);
      p3d.fill(0);
      p3d.textFont(font, 16);
      p3d.text("Worst Creature", 830, 310);
      p3d.text("Median Creature", 990, 310);
      p3d.text("Best Creature", 1150, 310);
    }
  }
}
