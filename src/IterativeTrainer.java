public class IterativeTrainer {
    private Network network;
    private int weightNum = 0;
    public int[] weights; //public just for unit testing
    private int w = 0;

    //# of "steps" on each side of the positive/negative value for our weights
    //if 4, our weight range will be -1, -0.75, ...
    //we use ints instead of doing +0.25 due to float fuzziness
    //we divide steps into a ratio from -1 to 1 when transposing the weights onto the network
    private int steps;

    //these are essentially maps to optimize our training tick re: looking up what weights to modify
    Neuron[] neuronLookup;
    int[] weightLookup;

    //if having trouble training, consider progressively increasing the step count and trying again
    public IterativeTrainer(Network network, int steps) {
        this.network = network;

        this.steps = steps;

        //figure out total # of weights
        int neuronNum = 0;
        for (Layer layer: network.layers) {
            for (Neuron neuron: layer.neurons) {
                weightNum += neuron.weights.length;
                neuronNum++;
            }
        }
        weights = new int[weightNum];

        //create lookup maps, initialize weightings
        int top = 0;
        neuronLookup = new Neuron[weightNum];
        weightLookup = new int[weightNum];
        for (Layer layer: network.layers) {
            for (Neuron neuron: layer.neurons) {
                for (int i = 0; i < neuron.weights.length; i++) {
                    //map
                    neuronLookup[top] = neuron;
                    weightLookup[top] = i;

                    //initialize
                    weights[top] = -steps;
                    neuron.weights[i] = -1;

                    top++;
                }
            }
        }

        //this is so we don't skip the first training cycle
        weights[0]--;

        //inside pow: *2 because we do both negative and positive weights, +1 for weight 0
        //iterationsMax = (int)Math.pow(steps*2+1, weightNum);
        //this is disabled b/c of int maxes
    }

    public boolean train() {
        //increment weights
        if (weights[w] == steps) { //weight we're targeting is maxed
            //move to the right to carry increment, resetting any maxed along the way
            do {
                weights[w] = -steps;
                reWeight();
                w++;
            } while(weights[w] == steps && w < weightNum-1);
            weights[w]++;
            reWeight();
            w = 0;
        } else { //weight we're targeting isn't maxed
            weights[w]++;
            reWeight();
        }

        //return true if  there is still training to do
        return weights[weightNum-1] != steps+1;
    }

    private void reWeight() {
        neuronLookup[w].weights[weightLookup[w]] = (double)weights[w] / steps;
    }
}
