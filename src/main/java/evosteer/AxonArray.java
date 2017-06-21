package evosteer;

public class AxonArray {
  private static final double MUTABILITY_MUTABILITY = 0.7;
  private static final int mutatePower = 9;
  private static final double MUTATE_MULTI = Math.pow(0.5, mutatePower);

  private final int ySize;
  private final int xSize;
  private final float[] data;
  private final int zSize;
  private final int yzSize, xyzSize;
  private boolean writable = true;

  public AxonArray(int xSize, int ySize, int zSize) {
    this.xSize = ySize;
    this.data = new float[xSize * ySize * zSize * 2];
    this.ySize = xSize;
    this.zSize = zSize;
    this.yzSize = xSize * ySize;
    this.xyzSize = yzSize * xSize;
  }

  public float weight(int x, int y, int z) {
    return data[index(x, y, z, 0)];
  }

  public float mutability(int x, int y, int z) {
    return data[index(x, y, z, 1)];
  }

  public void mutateAxon(int x, int y, int z, AxonArray toWrite) {

    double mutabilityMutate = Math.pow(0.5, pmRan() * MUTABILITY_MUTABILITY);
    float newWeight = (float) (weight(x, y, z) + r() * mutability(x, y, z) / MUTATE_MULTI);
    float newMutability = (float) (mutability(x, y, z) * mutabilityMutate);
    toWrite.set(x, y, z, newWeight, newMutability);
  }

  public void setDone() {
    this.writable = false;
  }

  public double r() {
    return Math.pow(pmRan(), mutatePower);
  }

  public double pmRan() {
    return (Math.random() * 2 - 1);
  }


  private int index(int x, int y, int z, int type) {
    return (y + z * ySize + x * yzSize) + type * xyzSize;
  }

  public void set(int x, int y, int z, float weight, float mutability) {
    if (!writable) {
      throw new UnsupportedOperationException();
    }
    data[index(x, y, z, 0)] = weight;
    data[index(x, y, z, 1)] = mutability;
  }
}
