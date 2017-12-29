/*
Given grid w/ 10 slots
cell must move back and forth to [0] and [9]
this is our PoC for saying food is at one end, water at the other, cell needs both
must utilize memory for this to work, ie. achieve oscillation
ie. the same set of inputs produces different outputs at different times
*/
public class Scenario2 {
    private int slots = 10; //0-indexed inclusive
    private MemoryUnit mu; //we need to clear this after each test as to not pollute other tests

    public Scenario2() {
        //make network
        mu = new MemoryUnit(1);
        Layer layer1 = new Layer(new Neuron[]{
                new MemoryNeuron(new double[2], mu),
                new ForgetNeuron(new double[2], mu),
        });
        Layer layer2 = new Layer(new Neuron[]{
                new Neuron(new double[4]),
                new Neuron(new double[4]),
        });
        Network network = new Network(new Layer[]{layer1, layer2});

        //initialize trainer
        IterativeTrainer trainer = new IterativeTrainer(network, 1);

        //train/test loop
        int c = 0;
        while (trainer.train()) {
            //debugging
            //mem
            /*network.layers[0].neurons[0].weights[0] = 1;
            network.layers[0].neurons[0].weights[1] = 0;
            //forget
            network.layers[0].neurons[1].weights[0] = -1;
            network.layers[0].neurons[1].weights[1] = 1;
            //left
            network.layers[1].neurons[0].weights[0] = 0;
            network.layers[1].neurons[0].weights[1] = 0;
            network.layers[1].neurons[0].weights[2] = 1;
            network.layers[1].neurons[0].weights[3] = 0;
            //right
            network.layers[1].neurons[1].weights[0] = 0;
            network.layers[1].neurons[1].weights[1] = 0;
            network.layers[1].neurons[1].weights[2] = -1;
            network.layers[1].neurons[1].weights[3] = 1;*/

            //test network
            if (testNetwork(network)) {
                System.out.print("SUCCESS: ");
                Util.outputNetwork(network, true);
                break;
            }

            //stats
            if (c % 100000000 == 0) {
                System.out.print("Update: ");
                Util.outputNetwork(network, true);
            }
            c++;
        }
        System.out.println("Done: ");
    }

    private boolean testNetwork(Network network) {
        //clear memory
        mu.forget();

        //we're going to train for a network that moves right first then reflects
        //for it to move left first we need a 2-neuron layer at the start to basically invert the inputs
        //this is so when a memory store triggers it is storing a 1 instead of a zero
        //move right
        for (int i = 0; i < slots; i++) {
            if (!testTick(network, (double)i/slots, 0, 1)) return false;
        }
        //reflect
        if (!testTick(network, (double)slots/slots, 1, 0)) return false;
        //move left
        for (int i = slots-1; i > 0; i--) {
            if (!testTick(network, (double)i/slots, 1, 0)) return false;
        }
        //reflect
        if (!testTick(network, 0d, 0, 1)) return false;
        //move right
        for (int i = 0; i < slots; i++) {
            if (!testTick(network, (double)i/slots, 0, 1)) return false;
        }
        //reflect
        if (!testTick(network, (double)slots/slots, 1, 0)) return false;
        //move left
        for (int i = slots-1; i > 0; i--) {
            if (!testTick(network, (double)i/slots, 1, 0)) return false;
        }
        //reflect
        if (!testTick(network, 0d, 0, 1)) return false;
        //etc...

        return true;
    }

    private boolean testTick(Network network, double cellX, double moveLeft, double moveRight) {
        double[] outputs = network.tick(new double[]{cellX});
        return outputs[0] == moveLeft && outputs[1] == moveRight;
    }
}
