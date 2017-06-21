package evosteer;

import static processing.core.PApplet.abs;

public enum Menu {
  WELCOME, // 0
  MAIN_MENU, // 1
  INIT_NEW_CREATURES, // 2
  MENU_3, // 3
  MAIN_MENU_SIMULATING, // 4
  RENDERING_SIMULATION, // 5
  SORT_CREATURES_AFTER_SIMULATION, // 6
  MENU_7, // 7
  ALL_CREATURES_SORT_ANIMATION, // 8
  MENU_9, // 9
  KILL_HALF, // 10
  MENU_11, // 11
  CREATE_OFFSPRINGS, // 12
  MENU_13;

  public boolean condition1() {
    return ordinal() % 2 == 1 && abs(ordinal() - 10) <= 3;
  }

  public boolean condition2() {
    return abs(ordinal()-9) <= 2;
  }

  public boolean condition3() {
    return ordinal() >= 9;
  }
}
