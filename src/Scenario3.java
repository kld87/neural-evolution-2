/*
Given a 2D grid, food source
cell must search grid to find food, then stop when it finds it
seems solveable (static start) w/ 2L, 3N, 1M, 1S or 2L 4N 0M 1S
solveable (dynamic start) w/ 2L, 4N, 1M, 1S
*/

public class Scenario3 {
    private int slots = 10; //0-indexed inclusive grid row/column size
    private int startX = 5; //cell start X for static start tests
    private int startY = 5; //cell start Y for static start tests
    private String path; //for debugging

    public Scenario3(int layersMax, int neuronsMax, int memoryMax, int stepsMax, int mutateChances) {
        Network best;
        Network working;
        Network temp;
        int bestScore = 0;
        int workingScore = 0;
        int tempScore = 0;

        //init trainer
        DynamicTrainer trainer = new DynamicTrainer(3, 4, layersMax, neuronsMax, memoryMax, stepsMax);
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

            //the idea w/ the below loop is to give the network a chance to mutate and improve before discarding
            //this is partially because we don't want to get stuck with a "best" network that has maxed its potential but monopolizes our attempts
            //instead, we give networks n tries to improve, and if they do, we reset the counter, until they stop improving
            //then we re-loop our outer loop and switch phases
            workingScore = 0;
            for (int i = 0; i < mutateChances; i++) {
                //make a clone of our working network
                temp = Util.cloneNetwork(working);
                trainer.mutateNetwork(temp);

                //test temp network
                tempScore = testNetwork(temp);

                //did we improve over working?
                if (tempScore > workingScore) { //we have to use > vs >= (unlike below for picking best) because we reset iterator
                    working = temp;
                    workingScore = tempScore;
                    //reset our iterator since we improved
                    i = 0;
                } else if (workingScore == 0) { //tempScore == workingScore == 0, don't bother giving this network another chance
                    break;
                }

                //did this network outperform previous best network? if so save for mutation/re-trial
                if (tempScore >= bestScore || tempScore == -1) { //we use >= vs > to help prevent getting a "stale" best
                    if (tempScore > bestScore || tempScore == -1) {
                        System.out.println(path);
                        bestScore = tempScore;
                        System.out.print("New best score: " + bestScore + " ");
                        Util.outputNetwork(temp, false);
                    }
                    best = temp;

                }

                //did we solve?
                if (checkSuccess(tempScore, temp)) break outerLoop;
            }

            //increment phase and re-loop
            phase++;
        }
    }

    private boolean checkSuccess(int score, Network network) {
        if (score == -1) {
            //output paths for four ~corners
            testTick(network, startX, startY, 1, 1);
            System.out.println(path);
            testTick(network, startX, startY, slots-1, 1);
            System.out.println(path);
            testTick(network, startX, startY, 1, slots-1);
            System.out.println(path);
            testTick(network, startX, startY, slots-1, slots-1);
            System.out.println(path);

            //success message
            System.out.print("SUCCESS: ");
            Util.outputNetwork(network, true);
            return true;
        }
        return false;
    }

    private int testNetwork(Network network) {
        //basic test for four ~corners
        if (!testTick(network, startX, startY, 1, 1)) return 0;
        if (!testTick(network, startX, startY, slots-1, 1)) return 1;
        if (!testTick(network, startX, startY, 1, slots-1)) return 2;
        if (!testTick(network, startX, startY, slots-1, slots-1)) return 3;

        int score = 0;
        //try all potential food locations from static start
        for (int fx = 0; fx <= slots; fx++) {
            for (int fy = 0; fy <= slots; fy++) {
                if (testTick(network, startX, startY, fx, fy)) {
                    score++;
                } else {
                    return score;
                }
            }
        }

        //try all potential food locations from all potential start locations
        for (int x = 0; x <= slots; x++) {
            for (int y = 0; y <= slots; y++) {
                for (int fx = 0; fx <= slots; fx++) {
                    for (int fy = 0; fy <= slots; fy++) {
                        if (testTick(network, x, y, fx, fy)) {
                            score++;
                        } else {
                            return score;
                        }
                    }
                }
            }
        }

        return -1; //-1 indicates perfect score
    }

    private boolean testTick(Network network, int x, int y, int fx, int fy) {
        double[] outputs; //up, down, left, right
        boolean foodAdjacent;
        int turns = 0;
        path = x + ":" + y + "-" + fx + ":" + fy + " --- ";

        //prep network
        Util.clearMemoryUnits(network); //clear mus

        do {
            //tick network
            foodAdjacent = Math.abs(x-fx) <= 1 && Math.abs(y-fy) <= 1;
            outputs = network.tick(new double[]{(double)x/slots, (double)y/slots, foodAdjacent ? 1 : 0});

            //analyze/process response
            if (foodAdjacent) { //next to food, should stay still
                if (outputs[0] == 0 && outputs[1] == 0 && outputs[2] == 0 && outputs[3] == 0) { //stayed still
                    path += "S:S";
                    break;
                } else { //tried to move
                    path += "M:M";
                    return false;
                }
            } else { //not next to food, should move
                if (outputs[0] == 1 || outputs[1] == 1 || outputs[2] == 1 || outputs[3] == 1) { //tried to move
                    //bad movement
                    if (outputs[0] == 1 && outputs[1] == 1) return false; //tried to move up & down at once
                    if (outputs[2] == 1 && outputs[3] == 1) return false; //tried to move left & right at once
                    if (outputs[0] == 1 && y == 0) return false; //tried to move into wall (up)
                    if (outputs[1] == 1 && y == slots) return false; //tried to move into wall (down)
                    if (outputs[2] == 1 && x == 0) return false; //tried to move into wall (left)
                    if (outputs[3] == 1 && x == slots) return false; //tried to move into wall (right)

                    //good movement
                    if (outputs[0] == 1) y--; //up
                    if (outputs[1] == 1) y++; //down
                    if (outputs[2] == 1) x--; //left
                    if (outputs[3] == 1) x++; //right

                    //path tracking
                    path += x + ":" + y + " ";
                } else { //stayed still
                    path += "F:F";
                    return false;
                }
            }

            //if we keep moving but haven't found food, eventually give up, since we're probably in a loop
            turns++;
            if (turns > (slots*slots*4)) {
                path += "T:T";
                return false;
            }
        } while (!foodAdjacent);

        return true;
    }
}
