public class ForgetNeuron extends Neuron{
    public MemoryUnit memoryUnit;

    public ForgetNeuron(double[] weights, MemoryUnit memoryUnit) {
        super(weights);
        this.memoryUnit = memoryUnit;
    }

    public double tick(double[] inputs) {
        double output = super.tick(inputs);

        if (output == 1) {
            memoryUnit.forget();
        }

        return output;
    }
}
