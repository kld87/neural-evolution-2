/*
Given a 1D grid
Cell must move towards/stay on top of food to survive
Inputs: cell's x, food's x
Outputs: move left, move right
*/
public class Scenario1 {
    private int slots = 10; //0-indexed inclusive

    public Scenario1() {
        //make network
        Layer layer = new Layer(new Neuron[]{
            new Neuron(new double[3]),
            new Neuron(new double[3]),
        });
        Network network = new Network(new Layer[]{layer});

        //initialize trainer
        IterativeTrainer trainer = new IterativeTrainer(network, slots);

        //train/test loop
        int c = 0;
        while (trainer.train()) {
            //debugging solution
            /*network.layers[0].neurons[0].weights[0] = 1;
            network.layers[0].neurons[0].weights[1] = -1;
            network.layers[0].neurons[0].weights[2] = 0.99;
            network.layers[0].neurons[1].weights[0] = -1;
            network.layers[0].neurons[1].weights[1] = 1;
            network.layers[0].neurons[1].weights[2] = 0.99;*/

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
        //we'll do some simple tests that are quick to compute first
        //move LTR
        if (!testTick(network, 0, 1, 0, 1)) return false;
        //move RTL1
        if (!testTick(network, 1, 0, 1, 0)) return false;
        //stay
        if (!testTick(network, 0.5, 0.5, 0, 0)) return false;

        //cell may be viable, do more advanced tests
        for (int cx = 0; cx <= slots; cx++) {
            for (int fx = 0; fx <= slots; fx++) {
                if (cx < fx) { //to the right
                    if (!testTick(network, (double)cx/slots, (double)fx/slots, 0, 1)) return false;
                } else if (cx > fx) { //to the left
                    if (!testTick(network, (double)cx/slots, (double)fx/slots, 1, 0)) return false;
                } else { //same spot
                    if (!testTick(network, (double)cx/slots, (double)fx/slots, 0, 0)) return false;
                }
            }
        }

        return true;
    }


    private boolean testTick(Network network, double cellX, double foodX, double moveLeft, double moveRight) {
        double[] outputs = network.tick(new double[]{cellX, foodX});
        return outputs[0] == moveLeft && outputs[1] == moveRight;
    }
}
