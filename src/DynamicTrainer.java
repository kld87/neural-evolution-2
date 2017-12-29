import java.util.Properties;

public class DynamicTrainer {
    private int inputNum;
    private int outputNum;
    private int layersMax;
    private int neuronsMax;
    private int memoryMax;
    private int stepsMax;

    public DynamicTrainer(int inputNum, int outputNum, int layersMax, int neuronsMax, int memoryMax, int stepsMax) {
        this.inputNum = inputNum;
        this.outputNum = outputNum;
        this.layersMax = layersMax;
        this.neuronsMax = neuronsMax;
        this.memoryMax = memoryMax;
        this.stepsMax = stepsMax;
    }

    public Network createNetwork() {
        //layer array
        int layerNum = Util.rng.nextInt(layersMax) + 1;
        Layer[] layers = new Layer[layerNum];
        //neuron vars
        Neuron[] neurons; //number of regular neurons
        int neuronNum;
        int inputNum = this.inputNum;
        //weight vars
        double[] weights;
        int steps = Util.rng.nextInt(stepsMax) + 1; //TODO: this needs to be remembered outside this function re: mutateNetwork comment
        //memory vars
        int memoryNum = layerNum > 1 ? Util.rng.nextInt(memoryMax+1) : 0; //ternary b/c we don't want memory in the last layer
        int[] memoryPerLayer = new int[layerNum];
        int neuronNumTotal; //total number of neurons including memory/forget neurons
        MemoryUnit mu = new MemoryUnit(0); //we don't need to init this re: how the logic works below, but we have to do this for the compiler

        //prep to evenly distribute memory neurons
        for (int l = 0; l < memoryNum; l++) {
            memoryPerLayer[Util.rng.nextInt(layerNum-1)]++; //-1 because no memory in the last layer
        }

        for (int l = 0; l < layerNum; l++) {
            //# of neurons
            if (l == layerNum-1) { //last layer, fixed at outputNum
                neuronNum = outputNum;
            } else { //random number between 1 and neuronsMax
                neuronNum = Util.rng.nextInt(neuronsMax) + 1;
            }

            //make neurons
            neuronNumTotal = neuronNum + memoryPerLayer[l]*2; //*2 re: memory+forget
            neurons = new Neuron[neuronNumTotal]; //mind memory+forget to be added later
            for (int n = 0; n < neuronNumTotal; n++) {
                //make weights
                weights = new double[inputNum+1]; //+1 for bias
                for (int w = 0; w < weights.length; w++) {
                    weights[w] = generateWeight(steps);
                }

                //make neuron w/ weights
                if (n < neuronNum) { //regular neuron
                    neurons[n] = new Neuron(weights);
                } else { //memory/forget neuron
                    if (neurons[n-1] instanceof MemoryNeuron) { //add forget neuron to pair w/ previous memory neuron
                        neurons[n] = new ForgetNeuron(weights, mu);
                    } else { //new memory neuron w/ mu
                        mu  = new MemoryUnit(inputNum);
                        neurons[n] = new MemoryNeuron(weights, mu);
                    }
                }
            }

            //make layer w/ neurons
            layers[l] = new Layer(neurons);

            //# inputs for next layer will be # of neurons from this layer
            inputNum = neuronNumTotal + inputNum*memoryPerLayer[l];
        }

        //make cloned network
        Network network = new Network(layers);
        //store step count we generated in network's metadata, to use again during mutation of a network needed
        network.metadata.setProperty("steps", "" + steps);

        return network;
    }

    public void mutateNetwork(Network network) {
        int steps = Integer.parseInt(network.metadata.getProperty("steps"));
        do {
            int n = Util.rng.nextInt(layersMax * neuronsMax * outputNum + 1);
            for (int i = 0; i < n; i++) {
                Layer layer = network.layers[Util.rng.nextInt(network.layers.length)];
                Neuron neuron = layer.neurons[Util.rng.nextInt(layer.neurons.length)];
                neuron.weights[Util.rng.nextInt(neuron.weights.length)] = generateWeight(steps);
            }
        } while (Util.rng.nextBoolean());
    }

    private double generateWeight(int steps) {
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
