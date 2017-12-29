public class MemoryUnit {
    public double[] memory;

    public MemoryUnit(int inputNum) {
        memory = new double[inputNum];
    }

    public void store(double[] values) {
        memory = values.clone();
    }

    public void forget() {
        memory = new double[memory.length];
    }
}
