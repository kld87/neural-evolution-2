public class MemoryNeuron extends Neuron {
    public MemoryUnit memoryUnit;

    public MemoryNeuron(double[] weights, MemoryUnit memoryUnit) {
        super(weights);
        this.memoryUnit = memoryUnit;
    }

    public double tick(double[] inputs) {
        double output = super.tick(inputs);

        if (output == 1) {
            memoryUnit.store(inputs);
        }

        return output;
    }
}
