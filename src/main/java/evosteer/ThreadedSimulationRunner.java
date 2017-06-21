package evosteer;

import java.util.concurrent.*;

public class ThreadedSimulationRunner {
  private static final ExecutorService executor = Executors.newFixedThreadPool(4);

  public static void run(Creature[] creatures) {
    Future<?>[] futures = new Future[creatures.length];
    for (int i = 0; i < creatures.length; i++) {
      int curr = i; // because java won't allow to use i directly in lambdas
      futures[i] = executor.submit(()->{
        EvolutionState state = new EvolutionState();
        state.resetState(creatures[curr]);
        for (int s = 0; s < 900; s++) {
          state.simulateCurrentCreature();
        }
        state.setAverages();
        state.setFitness(creatures, curr);
      });
    }
    for (Future<?> future : futures) {
      try {
        future.get(Integer.MAX_VALUE, TimeUnit.DAYS);
      } catch (InterruptedException e) {
        e.printStackTrace();
        return;
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      } catch (TimeoutException e) {
        throw new Error(e); //should never happen
      }
    }
  }
}
