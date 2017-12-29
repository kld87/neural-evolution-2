/*
(advanced version of Scenario3, copy-pasted then modified, see it for control explaination)
Given a 2D grid, food source, water source
cell must find food, water, and move between them to stay alive
*/

public class Scenario4 {
    private int slots = 10; //0-indexed inclusive grid row/column size, NOTE: if modifying this, validate static test numbers, also it should be divisible by 2
    private String path; //for debugging

    //constructor for fixed trainer, tweak structure as desired
    public Scenario4(int steps, int threshold) {
        //make network
        MemoryUnit mu1 = new MemoryUnit(6);
        Network network = new Network(new Layer[]{
           new Layer(new Neuron[]{
                   new Neuron(new double[7]),
                   new Neuron(new double[7]),
                   new Neuron(new double[7]),
                   new Neuron(new double[7]),
                   new Neuron(new double[7]),
                   new Neuron(new double[7]),
                   new MemoryNeuron(new double[7], mu1),
                   new ForgetNeuron(new double[7], mu1),
           }),
            new Layer(new Neuron[]{
                new Neuron(new double[25]),
                new Neuron(new double[25]),
                new Neuron(new double[25]),
                new Neuron(new double[25]),
            }),
        });

        //trainer
        FixedTrainer trainer = new FixedTrainer(steps, threshold);

        //train loop vars
        int bestScore = 0;
        int tempScore;
        Network temp;

        //train loop
        while (true) {
            temp = Util.cloneNetwork(network);
            trainer.mutateNetwork(temp);
            tempScore = testNetwork(temp);

            //did we solve?
            if (checkSuccess(tempScore, temp)) break;

            //new high score?
            if (tempScore > bestScore) {
                network = temp;
                bestScore = tempScore;
                System.out.print("New best score: " + bestScore + " ");
                Util.outputNetwork(temp, false);
                //System.out.println(path);
            } else if (tempScore == bestScore) { //keep if we didn't make things worse in case we're on a path to improvement
                network = temp;
            }
        }
    }

    //construct for dynamic trainer
    public Scenario4(int layersMax, int neuronsMax, int memoryMax, int stepsMax, int mutateChances) {
        Network best;
        Network working;
        Network temp;
        int bestScore = 0;
        int workingScore = 0;
        int tempScore = 0;

        //init trainer
        DynamicTrainer trainer = new DynamicTrainer(6, 4, layersMax, neuronsMax, memoryMax, stepsMax);
        best = trainer.createNetwork(); //needed to stop compile error below re: execution order

        int phase = 0;
        outerLoop:
        while (true) {
            //create network
            if (phase % 2 == 0) { //new random network
                working = trainer.createNetwork();
            } else { //mutated version of previous best
                working = Util.cloneNetwork(best);
            }

            //test/mutate network
            workingScore = 0;
            for (int i = 0; i < mutateChances; i++) {
                //make a clone of our working network
                temp = Util.cloneNetwork(working);
                trainer.mutateNetwork(temp);

                //test temp network
                tempScore = testNetwork(temp);

                //did we solve?
                if (checkSuccess(tempScore, temp)) break outerLoop;

                //did we improve over working?
                if (tempScore >= workingScore) { //see below re: >= vs >
                    if (tempScore > workingScore) { //reset our iterator if we improved
                        i = 0;
                    } else if (workingScore == 0) { //don't bother giving more chances if this network is a dud
                        break;
                    }
                    working = temp;
                    workingScore = tempScore;
                }

                //did this network outperform previous best network? if so save for mutation/re-trial
                if (tempScore >= bestScore) { //we use >= vs > to help prevent getting a "stale" best
                    if (tempScore > bestScore) {
                        bestScore = tempScore;
                        System.out.print("New best score: " + bestScore + " ");
                        Util.outputNetwork(temp, false);
                        //System.out.println(path);
                    }
                    best = temp;
                }
            }

            //increment phase and re-loop
            phase++;
        }
    }

    private boolean checkSuccess(int score, Network network) {
        if (score == -1) {
            System.out.print("SUCCESS: ");
            Util.outputNetwork(network, true);
            return true;
        }
        return false;
    }

    private int testNetwork(Network network) {
        int score = 0;

        //fixed tests, incrementing by half-slots
        //potential score should be 729, 3^6 for this phase
        //TODO: hard-code each and randomize the order, in case the incremental nature + scoring could favour w/ rigid networks?
        for (int x = slots; x >= 0; x -= slots/2) { //we're starting x at slots instead of 0 here so our first test doesn't start with everything stacked, for which a network that doesn't move would pass
            for (int y = 0; y <= slots; y += slots/2) {
                for (int fx = 0; fx <= slots; fx += slots/2) {
                    for (int fy = 0; fy <= slots; fy += slots/2) {
                        for (int wx = 0; wx <= slots; wx += slots/2) {
                            for (int wy = 0; wy <= slots; wy += slots/2) {
                                if (testTick(network, x, y, fx, fy, wx, wy)) {
                                    score++;
                                } else {
                                    return score;
                                }
                            }
                        }
                    }
                }
            }
        }

        //500 static random tests
        for (int[] inputs : Util.scenario4StaticTests) {
            if (testTick(network, inputs[0], inputs[1], inputs[2], inputs[3], inputs[4], inputs[5])) {
                score++;
            } else {
                return score;
            }
        }

        //try all potential food/water locations from dynamic start
        //potential score of 10^6 = 1M
        for (int x = 0; x <= slots; x++) {
            for (int y = 0; y <= slots; y++) {
                for (int fx = 0; fx <= slots; fx++) {
                    for (int fy = 0; fy <= slots; fy++) {
                        for (int wx = 0; wx <= slots; wx++) {
                            for (int wy = 0; wy <= slots; wy++) {
                                if (testTick(network, x, y, fx, fy, wx, wy)) {
                                    score++;
                                } else {
                                    return score;
                                }
                            }
                        }
                    }
                }
            }
        }

        return -1; //-1 indicates success, all tests passed
    }

    private boolean testTick(Network network, int x, int y, int fx, int fy, int wx, int wy) {
        double[] outputs; //up, down, left, right
        boolean foodAdjacent;
        boolean waterAdjacent;
        int resourceMax = slots*slots*3;
        int hunger = resourceMax;
        int thirst = resourceMax;
        int turns = 0;

        //prep network
        Util.clearMemoryUnits(network); //clear mus
        path = fx + ":" + fy + " & " + wx + ":" + wy + " --- ";

        do {
            //has the cell died?
            if (hunger <= 0 || thirst <= 0) {
                return false;
            }

            //calc adjacency
            foodAdjacent = Math.abs(x-fx) <= 1 && Math.abs(y-fy) <= 1;
            waterAdjacent = Math.abs(x-wx) <= 1 && Math.abs(y-wy) <= 1;

            //eat/drink
            if (foodAdjacent) hunger += slots;
            if (waterAdjacent) thirst += slots;
            //caps
            if (hunger > resourceMax) hunger = resourceMax;
            if (thirst > resourceMax) thirst = resourceMax;

            //tick network
            outputs = network.tick(new double[]{
                (double)x/slots,
                (double)y/slots,
                foodAdjacent ? 1 : 0,
                waterAdjacent ? 1 : 0,
                1 - (double)hunger/resourceMax,
                1 - (double)thirst/resourceMax,
            });

            //process outputs, we're more forgiving here re: "bad" moves vs. Scenario3 because we have our hunger/thirst limits
            if (y > 0 && outputs[0] == 1) y--; //up
            if (y < slots && outputs[1] == 1) y++; //down
            if (x > 0 && outputs[2] == 1) x--; //left
            if (x < slots && outputs[3] == 1) x++; //right

            path += x + ":" + y + " ";

            //prepare for next loop
            hunger--;
            thirst--;
            turns++;
        } while (turns < slots*slots*6);

        return true;
    }
}
