/*
This is essentially a slimmed-down version of DynamicTrainer
*/

public class FixedTrainer {
    private int steps;
    private int threshold;

    public FixedTrainer(int steps, int threshold) {
        this.steps = steps;
        this.threshold = threshold;
    }

    public void mutateNetwork(Network network) {
        do {
            int n = Util.rng.nextInt(threshold + 1);
            for (int i = 0; i < n; i++) {
                Layer layer = network.layers[Util.rng.nextInt(network.layers.length)];
                Neuron neuron = layer.neurons[Util.rng.nextInt(layer.neurons.length)];
                neuron.weights[Util.rng.nextInt(neuron.weights.length)] = generateWeight();
            }
        } while (Util.rng.nextBoolean());
    }

    private double generateWeight() {
        double weight;

        //we give preference to 0, then +/-1, then a step weight, TODO: tune?
        int pivot = Util.rng.nextInt(7); //0-6
        if (pivot < 4) { //0, 1, 2, 3
            weight = 0;
        } else if (pivot < 6) { //4, 5
            weight = 1;
        } else { //6
            weight = Util.rng.nextInt(steps + 1);
        }

        //make negative?
        if (Util.rng.nextBoolean()) weight *= -1; //TODO: this can produce negative zero (-0), fix? I think it's benign...

        //normalize
        weight /= steps;

        return weight;
    }
}
