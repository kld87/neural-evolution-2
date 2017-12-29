import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

class IterativeTrainerTest {
    @Test
    void testTrainer() {
        //simple test
        Layer l1 = new Layer(new Neuron[] {
            new Neuron(new double[3]),
        });
        Network n = new Network(new Layer[]{l1});
        IterativeTrainer t = new IterativeTrainer(n, 1);

        int[][] weights = new int[][] {
            {-1, -1, -1},
            {0, -1, -1},
            {1, -1, -1},
            {-1, 0, -1},
            {0, 0, -1},
            {1, 0, -1},
            {-1, 1, -1},
            {0, 1, -1},
            {1, 1, -1},
            {-1, -1, 0},
            {0, -1, 0},
            {1, -1, 0},
            {-1, 0, 0},
            {0, 0, 0},
            {1, 0, 0},
            {-1, 1, 0},
            {0, 1, 0},
            {1, 1, 0},
            {-1, -1, 1},
            {0, -1, 1},
            {1, -1, 1},
            {-1, 0, 1},
            {0, 0, 1},
            {1, 0, 1},
            {-1, 1, 1},
            {0, 1, 1},
            {1, 1, 1}
        };
        int i = 0;
        while (t.train()) {
            Assertions.assertArrayEquals(weights[i], t.weights, "step");
            Assertions.assertArrayEquals(Arrays.stream(weights[i]).asDoubleStream().toArray(), n.layers[0].neurons[0].weights, "normalized weight");
            i++;
        }
        //should iterate steps*2+1 ^ neurons, because steps ranges negative to positive plus zero, for each possible combination for # of neurons
        Assertions.assertEquals(27, i, "don't overshoot");

        //complex text (w/ steps)
        i = 0;
        t = new IterativeTrainer(n, 4);
        while (t.train()) {
            switch (i) {
                case 0:
                    Assertions.assertArrayEquals(new int[]{-4, -4, -4}, t.weights, "step");
                    Assertions.assertArrayEquals(new double[]{-1, -1, -1}, n.layers[0].neurons[0].weights, "normalized weight");
                    break;
                case 1:
                    Assertions.assertArrayEquals(new int[]{-3, -4, -4}, t.weights, "step");
                    Assertions.assertArrayEquals(new double[]{-0.75, -1, -1}, n.layers[0].neurons[0].weights, "normalized weight");
                    break;
                case 182:
                    Assertions.assertArrayEquals(new int[]{-2, -2, -2}, t.weights, "step");
                    Assertions.assertArrayEquals(new double[]{-0.5, -0.5, -0.5}, n.layers[0].neurons[0].weights, "normalized weight");
                    break;
                case 364:
                    Assertions.assertArrayEquals(new int[]{0, 0, 0}, t.weights, "step");
                    Assertions.assertArrayEquals(new double[]{0, 0, 0}, n.layers[0].neurons[0].weights, "normalized weight");
                    break;
                case 716:
                    Assertions.assertArrayEquals(new int[]{1, 3, 4}, t.weights, "step");
                    Assertions.assertArrayEquals(new double[]{0.25, 0.75, 1}, n.layers[0].neurons[0].weights, "normalized weight");
                    break;
                case 728:
                    Assertions.assertArrayEquals(new int[]{4, 4, 4}, t.weights, "step");
                    Assertions.assertArrayEquals(new double[]{1, 1, 1}, n.layers[0].neurons[0].weights, "normalized weight");
                    break;
            }
            i++;
        }
        //see comment above re: where 729 comes from
        Assertions.assertEquals(729, i, "don't overshoot");
    }
}