package evosteer;

import evosteer.render.RenderMenu;
import evosteer.render.RenderMain;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EvolutionSteer extends PApplet {
  public static final float windowSizeMultiplier = 0.8f;//1.4f;
  public static final  int SEED = 31; //7;  ;(

  public static final boolean haveGround = true;
  public static final int histBarsPerMeter = 5;
  public static final String fitnessUnit = "chomps";
  public static final String fitnessName = "Chomps";
  public static final float baselineEnergy = 0.0f;
  public static final int energyDirection = 1; // if 1, it'll count up how much energy is used.  if -1, it'll count down from the baseline energy, and when energy hits 0, the creature dies.
  public static final float FRICTION = 4;
  public static final  float bigMutationChance = 0.03f;

  public static float foodAngleChange = 0.0f;

  public static final float pressureUnit = 500.0f/2.37f;
  public static final float energyUnit = 20;
  public static final float nauseaUnit = 5;

  public static final float postFontSize = 0.96f;
  public static final float scaleToFixBug = 1000;

  public static final float gravity = 0.006f;//0.007;
  public static final float airFriction = 0.95f;
  public static final float MIN_FOOD_DISTANCE = 1;
  public static final float MAX_FOOD_DISTANCE = 2.5f;

  public static final int minBar = -10;
  public static final int maxBar = 100;
  public static final int barLen = maxBar-minBar;

  public static final int[] p = {
          0, 10, 20, 30, 40, 50, 60, 70, 80, 90,
          100, 200, 300, 400, 500, 600, 700, 800, 900, 910, 920, 930, 940, 950, 960, 970, 980, 990, 999
  };
  public static final int BRAIN_WIDTH = 3;
  public static final float STARTING_AXON_VARIABILITY = 1.0f;
  public static final float AXON_START_MUTABILITY = 0.0005f;
  public static final int PATRON_COUNT = 75;
  public static final float TOTAL_PLEDGED = 183.39f;
  public static final int[] CREATURES_PER_PATRON = new int[PATRON_COUNT];


  public static int windowWidth = 1280;
  public static int windowHeight = 720;

  public static int simulationSpeed;
  public static boolean stepbystep;
  public static boolean stepbystepslow;
  public static int overallTimer = 0;

  public static float sliderX = 1170;
  public static boolean drag = false;

  public static String[] patronData;

  public static PFont font;
  // TODO: make it non-global
  public static ArrayList<Creature> creatureDatabase = new ArrayList<>(0);
  public static Creature[] creatureArray = new Creature[1000];
  public static ArrayList<Creature> creatureList = new ArrayList<>();

  int gensToDo = 0;

  float CAMERA_MOVE_SPEED = 0.03f;

  int menu = 0;
  int gen = -1;

  int genSelected = 0;
  int creaturesTested = 0;

  int statusWindow = -4;
  int prevStatusWindow = -4;

  boolean miniSimulation = false;
  int creatureWatching = 0;

  int[] creaturesInPosition = new int[1000];

  private boolean justGotBack = false;

  boolean slowDies;

  private RenderMain renderMain;
  private RenderMenu renderMenu;
  private EvolutionState state;

  static float toMuscleUsable(float f){
    return min(max(f,0.8f),1.2f);
  }

  @Override
  public void mouseWheel(MouseEvent event) {
    float delta = Math.signum(event.getCount());
    if (menu == 5 || statusWindow >= -3) {
      if (delta == -1) {
        state.zoomOut();
        textFont(font, postFontSize);
      } else if (delta == 1) {
        state.zoomIn();
        textFont(font, postFontSize);
      }
    }
  }
  @Override
  public void mousePressed() {
    if (gensToDo >= 1) {
      gensToDo = 0;
    }
    float mX = mouseX/windowSizeMultiplier;
    float mY = mouseY/windowSizeMultiplier;
    if (menu == 1 && gen >= 1 && abs(mY-365) <= 25 && abs(mX-sliderX-25) <= 25) {
      drag = true;
    }
  }

  void openMiniSimulation() {
    state.resetTimer();
    if (gensToDo == 0) {
      miniSimulation = true;
      int id;
      Creature cj;
      if (statusWindow <= -1) {
        cj = creatureDatabase.get((genSelected-1)*3+statusWindow+3);
        id = cj.id;
      } else {
        id = statusWindow;
        cj = creatureList.get(id);
      }
      state.resetState(cj);
      creatureWatching = id;
    }
  }
  void setMenu(int m) {
    menu = m;
    if (m == 1) {
      renderMain.drawGraph(gen, 975, 570);
    }
  }
  String zeros(int n, int zeros){
    String s = n+"";
    for(int i = s.length(); i < zeros; i++){
      s = "0"+s;
    }
    return s;
  }

  void startASAP() {
    setMenu(4);
    creaturesTested = 0;
    stepbystep = false;
    stepbystepslow = false;
  }
  @Override
  public void mouseReleased() {
    drag = false;
    miniSimulation = false;
    float mX = mouseX/windowSizeMultiplier;
    float mY = mouseY/windowSizeMultiplier;
    if (menu == 0 && abs(mX-windowWidth/2) <= 200 && abs(mY-400) <= 100) {
      setMenu(1);
    }else if (menu == 1 && gen == -1 && abs(mX-120) <= 100 && abs(mY-300) <= 50) {
      setMenu(2);
    }else if (menu == 1 && gen >= 0 && abs(mX-990) <= 230) {
      if (abs(mY-40) <= 20) {
        setMenu(4);
        simulationSpeed = 1;
        creaturesTested = 0;
        stepbystep = true;
        stepbystepslow = true;
      }
      if (abs(mY-90) <= 20) {
        setMenu(4);
        creaturesTested = 0;
        stepbystep = true;
        stepbystepslow = false;
      }
      if (abs(mY-140) <= 20) {
        if (mX < 990) {
          gensToDo = 1;
        } else {
          gensToDo = 1000000000;
        }
        startASAP();
      }
    }else if (menu == 3 && abs(mX-1030) <= 130 && abs(mY-684) <= 20) {
      gen = 0;
      setMenu(1);
    } else if (menu == 7 && abs(mX-1030) <= 130 && abs(mY-684) <= 20) {
      setMenu(8);
    } else if((menu == 5 || menu == 4) && mY >= windowHeight-40){
      if(mX < 90){
        for (int s = state.timer; s < 900; s++) {
          state.simulateCurrentCreature();
        }
        state.timer = 1021;
      }else if(mX >= 120 && mX < 360){
        simulationSpeed *= 2;
        if(simulationSpeed == 1024) simulationSpeed = 900;
        if(simulationSpeed >= 1800) simulationSpeed = 1;
      }else if(mX >= windowWidth-120){
        for (int s = state.timer; s < 900; s++) {
          state.simulateCurrentCreature();
        }
        state.timer = 0;
        creaturesTested++;
        for (int i = creaturesTested; i < 1000; i++) {
          state.resetState(creatureArray[i]);
          for (int s = 0; s < 900; s++) {
            state.simulateCurrentCreature();
          }
          state.setAverages();
          state.setFitness(creatureArray, i);
        }
        setMenu(6);
      }
    } else if(menu == 8 && mX < 90 && mY >= windowHeight-40){
      state.timer = 100000;
    } else if (menu == 9 && abs(mX-1030) <= 130 && abs(mY-690) <= 20) {
      setMenu(10);
    }else if (menu == 11 && abs(mX-1130) <= 80 && abs(mY-690) <= 20) {
      setMenu(12);
    }else if (menu == 13 && abs(mX-1130) <= 80 && abs(mY-690) <= 20) {
      setMenu(1);
    }
  }

  @Override
  public void settings() {
    noSmooth();
    size((int)(windowWidth*windowSizeMultiplier), (int)(windowHeight*windowSizeMultiplier),P3D);
  }

  @Override
  public void setup() {
    String[] prePatronData = loadStrings("PatronReport_2017-06-12.csv");
    patronData = new String[PATRON_COUNT];
    int lineAt = 0;
    for(int i = 0; i < prePatronData.length; i++){
      if(i != 0 && prePatronData[i].indexOf("Reward") == -1){
        patronData[lineAt] = prePatronData[i];
        lineAt++;
      }
    }
    for(int i = 0; i < PATRON_COUNT; i++){
      CREATURES_PER_PATRON[i] = 0;
    }
    frameRate(60);
    randomSeed(SEED);
    ellipseMode(CENTER);

    font = loadFont("Helvetica-Bold-96.vlw");
    textFont(font, 96);
    textAlign(CENTER);

    state = new EvolutionState();

    List<Float[]> percentile = new ArrayList<>();
    List<Integer[]> speciesCounts = new ArrayList<>();
    renderMain = new RenderMain(this, font, state, percentile, speciesCounts);
    renderMain.setup();

    renderMenu = new RenderMenu(this, renderMain, font, state, percentile, speciesCounts);
  }
  @Override
  public void draw() {
    scale(windowSizeMultiplier);
    if (menu == 0) {
      renderMenu.renderWelcomeMenu();
    }else if (menu == 1) {
      renderMenu.renderMainMenu(gen, genSelected, gensToDo);
      if (gensToDo >= 1) {
        gensToDo--;
        if (gensToDo >= 1) {
          startASAP();
        }
      }
    }else if (menu == 2) {
      Random rnd = new Random(SEED);
      for (int y = 0; y < 25; y++) {
        for (int x = 0; x < 40; x++) {
          int nodeNum = (int)(random(4, 8));
          int muscleNum = (int)(random(nodeNum, nodeNum*3));
          ArrayList<Node> n = new ArrayList<Node>(nodeNum);
          ArrayList<Muscle> m = new ArrayList<Muscle>(muscleNum);
          for (int i = 0; i < nodeNum; i++) {
            n.add(new Node(state, new Random(rnd.nextInt()), random(-1, 1), random(-1, 1), random(-1, 1),
                    0, 0, 0, 0.4f, random(0, 1))); //replaced all nodes' sizes with 0.4, used to be random(0.1,1), random(0,1)
          }
          for (int i = 0; i < muscleNum; i++) {
            int tc1 = 0;
            int tc2 = 0;
            if (i < nodeNum-1) {
              tc1 = i;
              tc2 = i+1;
            } else {
              tc1 = (int)(random(0, nodeNum));
              tc2 = tc1;
              while (tc2 == tc1) {
                tc2 = (int)(random(0, nodeNum));
              }
            }
            float s = 0.8f;
            if (i >= 10) {
              s *= 1.414;
            }
            float len = random(0.5f,1.5f);
            m.add(new Muscle(state, new Random(rnd.nextInt()), tc1, tc2, len, random(0.015f, 0.06f)));
          }
          float heartbeat = random(40, 80);
          creatureArray[y*40+x] = new Creature(state, new Random(rnd.nextInt()), null, y*40+x+1, new ArrayList<Node>(n), new ArrayList<Muscle>(m), 0, true, heartbeat, 1.0f, null, null);
          creatureArray[y*40+x].checkForOverlap();
          creatureArray[y*40+x].checkForLoneNodes();
          creatureArray[y*40+x].toStableConfiguration();
          creatureArray[y*40+x].moveToCenter();
        }
      }
      renderMenu.renderNew1000CreaturesMenu();
      setMenu(3);
    }else if(menu == 3){
      renderMenu.renderMenu3();
    }else if (menu == 4) {
      state.resetState(creatureArray[creaturesTested]);
      setMenu(5);
      if (!stepbystepslow) {
        simulateSingleGeneration();
        setMenu(6);
      }
    }
    if (menu == 5) {
      boolean simulating = state.timer <= 900;
      if (simulating) {
        keysToMoveCamera();
        for (int s = 0; s < simulationSpeed; s++) {
          if (state.timer < 900) {
            state.simulateCurrentCreature();
          }
        }
        state.setAverages();
        if (simulationSpeed < 30) {
          for (int s = 0; s < simulationSpeed; s++) {
            state.moveCamera();
          }
        } else {
          state.cameraAtCreature();
        }
      }
      boolean justFinished = state.timer == 900;
      renderMenu.renderMenu5(simulating, justFinished);
      if (justFinished) {
        state.setFitness(creatureArray, creaturesTested);
      }
      if (state.timer >= 1020) {
        setMenu(4);
        creaturesTested++;
        if (creaturesTested == 1000) {
          setMenu(6);
        }
        state.camX = 0;
      }
      if (state.timer >= 900) {
        state.timer += simulationSpeed;
      }
    }
    if (menu == 6) {
      renderMenu.renderMenu6(gen);
      if (stepbystep) {
        renderMain.drawScreenImage(creatureArray, creatureList, creaturesInPosition, gen, 0, renderMenu.gridBGColor);
        setMenu(7);
      } else {
        setMenu(10);
      }
    }
    if (menu == 8) {
      renderMenu.renderAllCreaturesMenu(gen);
      if (state.timer > 60*PI) {
        renderMain.drawScreenImage(creatureArray, creatureList, creaturesInPosition, gen, 1, renderMenu.gridBGColor);
        setMenu(9);
      }
    }
    float mX = mouseX/windowSizeMultiplier;
    float mY = mouseY/windowSizeMultiplier;
    prevStatusWindow = statusWindow;
    if (abs(menu-9) <= 2 && gensToDo == 0 && !drag) {
      if (abs(mX-639.5f) <= 599.5) {
        if (menu == 7 && abs(mY-329) <= 312) {
          statusWindow = creaturesInPosition[floor((mX-40)/30)+floor((mY-17)/25)*40];
        }
        else if (menu >= 9 && abs(mY-354) <= 312) {
          statusWindow = floor((mX-40)/30)+floor((mY-42)/25)*40;
        }
        else {
          statusWindow = -4;
        }
      }
      else {
        statusWindow = -4;
      }
    } else if (menu == 1 && genSelected >= 1 && gensToDo == 0 && !drag) {
      statusWindow = -4;
      if (abs(mY-250) <= 70) {
        if (abs(mX-990) <= 230) {
          float modX = (mX-760)%160;
          if (modX < 140) {
            statusWindow = floor((mX-760)/160)-3;
          }
        }
      }
    } else {
      statusWindow = -4;
    }
    if (menu == 10) {
      //Kill!
      for (int j = 0; j < 500; j++) {
        if(random(0,1) < getSB(gen)){
          float f = (float)(j)/1000;
          float rand = (pow(random(-1, 1), 3)+1)/2; //cube function
          slowDies = (f <= rand);
        }else{
          slowDies = (random(0,1) < 0.5);
        }
        int j2;
        int j3;
        if (slowDies) {
          j2 = j;
          j3 = 999-j;
        } else {
          j2 = 999-j;
          j3 = j;
        }
        Creature cj = creatureList.get(j2);
        cj.alive = true;
        Creature ck = creatureList.get(j3);
        ck.alive = false;
      }
      if (stepbystep) {
        renderMain.drawScreenImage(creatureArray, creatureList, creaturesInPosition, gen, 2, renderMenu.gridBGColor);
        setMenu(11);
      } else {
        setMenu(12);
      }
    }
    if (menu == 12) {
      justGotBack = true;
      for (int j = 0; j < 500; j++) {
        int j2 = j;
        if (!creatureList.get(j).alive) j2 = 999-j;
        Creature cj = creatureList.get(j2);
        Creature cj2 = creatureList.get(999-j2);

        creatureList.set(j2, cj.copyCreature(cj.id+1000,true,false));        //duplicate
        creatureList.set(999-j2, cj.modified(cj2.id+1000));   //mutated offspring 1
      }
      for (int j = 0; j < 1000; j++) {
        Creature cj = creatureList.get(j);
        creatureArray[cj.id-(gen*1000)-1001] = cj.copyCreature(-1,false,false);
      }
      renderMain.drawScreenImage(creatureArray, creatureList, creaturesInPosition, gen, 3, renderMenu.gridBGColor);
      gen++;
      if (stepbystep) {
        setMenu(13);
      } else {
        setMenu(1);
      }
    }
    if(menu%2 == 1 && abs(menu-10) <= 3){
      renderMain.renderScreenImage();
    }
    if (menu == 1 || gensToDo >= 1) {
      if (gen >= 1) {
        if (gen >= 5) {
          genSelected = round((sliderX - 760) * (gen - 1) / 410) + 1;
        } else {
          genSelected = round((sliderX - 760) * gen / 410);
        }
      }
      renderMenu.drawMenu1(gen, genSelected);
      if (justGotBack) justGotBack = false;
    }
    if (statusWindow >= -3) {
      int x, y, px, py;
      Creature cj;
      if (statusWindow >= 0) {
        cj = creatureList.get(statusWindow);
        if (menu == 7) {
          int id = ((cj.id-1)%1000);
          x = id%40;
          y = floor(id/40);
        } else {
          x = statusWindow%40;
          y = floor(statusWindow/40)+1;
        }
        px = x*30+55;
        py = y*25+10;
        if (px <= 1140) {
          px += 80;
        } else {
          px -= 80;
        }
      } else {
        cj = creatureDatabase.get((genSelected-1)*3+statusWindow+3);
        x = 760+(statusWindow+3)*160;
        y = 180;
        px = x;
        py = y;
      }
      renderMain.drawStatusWindow(cj, statusWindow, x, y, px, py);
      if (miniSimulation) {
        keysToMoveCamera();
        renderMain.drawMiniSimulation(cj, px, py);
        int shouldBeWatching = statusWindow;
        if (statusWindow <= -1) {
          cj = creatureDatabase.get((genSelected-1)*3+statusWindow+3);
          shouldBeWatching = cj.id;
        }
        if (creatureWatching != shouldBeWatching || prevStatusWindow == -4) {
          openMiniSimulation();
        }

      }
      if (statusWindow >= -3 && !miniSimulation) {
        openMiniSimulation();
      }
    }
    overallTimer++;
  }

  public void simulateSingleGeneration() {
    long time = -System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      state.resetState(creatureArray[i]);
      for (int s = 0; s < 900; s++) {
        state.simulateCurrentCreature();
      }
      state.setAverages();
      state.setFitness(creatureArray, i);
    }
    System.out.println(TimeUnit.NANOSECONDS.toMillis(time+System.nanoTime()));
  }

  float getSB(int g){
    return 1.0f;
    //return 0.7+0.3*cos(g*(2*PI)/50.0);
  }
  void keysToMoveCamera(){
    if(keyPressed){
      if(key == 'w'){
        state.camVA -= CAMERA_MOVE_SPEED;
      }
      if(key == 's'){
        state.camVA += CAMERA_MOVE_SPEED;
      }
      if(key == 'a'){
        state.camHA -= CAMERA_MOVE_SPEED;
      }
      if(key == 'd'){
        state.camHA += CAMERA_MOVE_SPEED;
      }
    }
    state.camVA = min(max(state.camVA,-PI*0.499f),-PI*0.001f);
  }
  @Override
  public void keyPressed(){
    if(key == 't'){
      foodAngleChange += 5.0/360.0*(2*PI);
      setMenu(1);
    }
    if(key == 'g'){
      foodAngleChange -= 5.0/360.0*(2*PI);
      setMenu(1);
    }
  }

  int[] getNewCreatureName(){
    float indexOfChoice = random(0,TOTAL_PLEDGED);
    float runningTotal = 0;
    for(int i = 0; i < patronData.length; i++){
      String[] parts = patronData[i].split(",");
      runningTotal += Float.parseFloat(parts[3]);
      if(runningTotal >= indexOfChoice){
        int[] result = new int[2];
        result[0] = i;
        result[1] = CREATURES_PER_PATRON[i];
        CREATURES_PER_PATRON[i]++;
        return result;
      }
    }
    return null;
  }
}
