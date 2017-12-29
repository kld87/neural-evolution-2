import java.util.ArrayList;

public class Layer {
    public Neuron[] neurons;
    public ArrayList<MemoryNeuron> memoryNeurons = new ArrayList<>(0); //public to assist w/ easy clearing

    public Layer(Neuron[] neurons) {
        this.neurons = neurons;

        //track any memory neurons b/c they have special behaviour (effectively this layer will produce extra outputs)
        //REMEMBER: we should pass an initialized neuron array into the constructor, re: this loop below, vs, adding/setting neurons after the fact..
        //...otherwise memoryNeurons won't be populated properly
        for (int i = 0; i < neurons.length; i++) {
            if (neurons[i] instanceof MemoryNeuron) {
                memoryNeurons.add((MemoryNeuron)neurons[i]);
            }
        }
    }

    public double[] tick(double[] inputs) {
        double[] outputs = new double[neurons.length + inputs.length*memoryNeurons.size()];

        for (int i = 0; i < neurons.length; i++) {
            outputs[i] = neurons[i].tick(inputs);
        }

        //extra outputs from memoryNeurons (if any)
        int top = neurons.length;
        for (MemoryNeuron mn : memoryNeurons) {
            for (int i = 0; i < mn.memoryUnit.memory.length; i++) {
                outputs[top] = mn.memoryUnit.memory[i];
                top++;
            }
        }

        return outputs;
    }
}
