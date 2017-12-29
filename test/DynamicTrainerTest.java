import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DynamicTrainerTest {
    @Test
    void testTrainer() {
        DynamicTrainer t = new DynamicTrainer(4, 4, 4, 4, 0, 4);

        //given that our trainer uses randomness, testing is awkward
        //we're basically going to play the odds and iterate a bunch of times until we hit all possible expected scenarios
        //otherwise we'll break after it's been statistically way too long to not have hit everything
        //in theory it's possible for this test to wrongly fail, but extremely unlikely
        Network n;
        int c;

        //input/output params
        for (int i = 0; i < 10000; i++) {
            n = t.createNetwork();
            Assertions.assertEquals(5, n.layers[0].neurons[0].weights.length, "first layer's neurons should have inputNum + 1 inputs");
            Assertions.assertEquals(4, n.layers[n.layers.length-1].neurons.length, "last layer should have 4 neurons/outputs");
        }


        //check # of layers
        for (int i = 1; i <= 4; i++) {
            c = 0;
            while (c < 10000) {
                n = t.createNetwork();
                if (n.layers.length == i) break;
                c++;
            }
            Assertions.assertTrue(c < 10000, "network with " + i + " layers");
        }

        //check # of neurons
        for (int i = 1; i <= 4; i++) {
            c = 0;
            while (c < 10000) {
                n = t.createNetwork();
                if (n.layers[0].neurons.length == i) break;
                c++;
            }
            Assertions.assertTrue(c < 10000, "layer with " + i + " neurons");
        }

        //check # of weights
        for (int i = 2; i <= 5; i++) { //2-5 not 1-4 because of biases
            c = 0;
            while (c < 10000) {
                n = t.createNetwork();
                if (n.layers.length == 1) continue; //since first layer has static inputs
                if (n.layers[1].neurons[0].weights.length == i) break;
                c++;
            }
            Assertions.assertTrue(c < 10000, "neuron with " + i + " weights");
        }

        //check weight range
        for (int i = -4; i <= 4; i++) { //-1 to 1 by quarters
            c = 0;
            while (c < 10000) {
                n = t.createNetwork();
                if (n.layers[0].neurons[0].weights[0] == (double)i/4) break;
                c++;
            }
            Assertions.assertTrue(c < 10000, "neuron with " + (double)i/4 + " weight value");
        }

        //TODO: memory neuron tests
        t = new DynamicTrainer(4, 4, 4, 4, 2, 4);

        //no memory neurons in the output layer
        for (int i = 0; i < 10000; i++) {
            n = t.createNetwork();
            for (Neuron neuron: n.layers[n.layers.length-1].neurons) {
                Assertions.assertFalse(neuron instanceof MemoryNeuron, "no memory neurons in last layer");
                Assertions.assertFalse(neuron instanceof ForgetNeuron, "no forget neurons in last layer");
            }
        }

        //memory/forget in the first layer
        c = 0;
        outerLoop:
        while (c < 10000) {
            n = t.createNetwork();
            for (int i = 0; i < n.layers[0].neurons.length; i++) {
                if (n.layers[0].neurons[i] instanceof MemoryNeuron && n.layers[0].neurons[i+1] instanceof ForgetNeuron) {
                    break outerLoop;
                }
            }
            c++;
        }
        Assertions.assertTrue(c < 10000, "memory/forget neurons in the first layer");

        //memory/forget in the hidden layers
        c = 0;
        outerLoop:
        while (c < 10000) {
            n = t.createNetwork();
            if (n.layers.length < 3) {
                continue;
            }
            for (int i = 0; i < n.layers[1].neurons.length; i++) {
                if (n.layers[1].neurons[i] instanceof MemoryNeuron && n.layers[1].neurons[i+1] instanceof ForgetNeuron) {
                    break outerLoop;
                }
            }
            c++;
        }
        Assertions.assertTrue(c < 10000, "memory/forget neurons in hidden layers");

        //minimum viable network test
        t = new DynamicTrainer(1, 1, 1, 1, 0, 1);
        n = t.createNetwork();
        Assertions.assertEquals(1, n.layers.length, "one layer");
        Assertions.assertEquals(1, n.layers[0].neurons.length, "one neuron");
        Assertions.assertEquals(2, n.layers[0].neurons[0].weights.length, "one neuron weight, one bias weight");

        //check weight range
        for (int i = -1; i <= 1; i++) { //-1, 0, 1
            c = 0;
            while (c < 10000) {
                n = t.createNetwork();
                if (n.layers[0].neurons[0].weights[0] == (double)i) break;
                c++;
            }
            Assertions.assertTrue(c < 10000, "neuron with " + i + " weight value");
        }
    }
}
