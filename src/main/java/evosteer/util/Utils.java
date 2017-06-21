package evosteer.util;

import evosteer.Creature;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

import static processing.core.PApplet.pow;
import static processing.core.PApplet.sq;

public class Utils {
  // TODO: use builtin sort
  public static ArrayList<Creature> quickSort(ArrayList<Creature> c) {
    if (c.size() <= 1) {
      return c;
    }
    else {
      ArrayList<Creature> less = new ArrayList<Creature>();
      ArrayList<Creature> more = new ArrayList<Creature>();
      ArrayList<Creature> equal = new ArrayList<Creature>();
      Creature c0 = c.get(0);
      equal.add(c0);
      for (int i = 1; i < c.size(); i++) {
        Creature ci = c.get(i);
        if (ci.d == c0.d) {
          equal.add(ci);
        }
        else if (ci.d < c0.d) {
          less.add(ci);
        }
        else {
          more.add(ci);
        }
      }
      ArrayList<Creature> total = new ArrayList<Creature>();
      total.addAll(quickSort(more));
      total.addAll(equal);
      total.addAll(quickSort(less));
      return total;
    }
  }

  public static float inter(int a, int b, float offset) {
    return a+(b-a)*offset;
  }

  public static float rand(Random r, float min, float max) {
    return r.nextFloat() * (max-min) + min;
  }

  public static float r(Random rand) {
    return pow(rand(rand, -1, 1), 19);
  }
}
