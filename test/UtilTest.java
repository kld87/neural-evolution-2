import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilTest {
    @Test
    void testClone() {
        //create orignal network
        MemoryUnit mu = new MemoryUnit(1);
        Network original = new Network(new Layer[]{
            new Layer(new Neuron[]{
                    new Neuron(new double[]{1, 1}),
                    new MemoryNeuron(new double[]{1, 1}, mu),
                    new ForgetNeuron(new double[]{1, 1}, mu),
            })
        });
        original.metadata.setProperty("test", "1");
        mu.memory[0] = 1;

        //create our clone netwrok
        Network cloned = Util.cloneNetwork(original);
        MemoryNeuron memoryNeuron;
        ForgetNeuron forgetNeuron;

        //basic tests
        Assertions.assertTrue(cloned.layers[0].neurons[0] instanceof Neuron, "regular neuron");
        Assertions.assertFalse(cloned.layers[0].neurons[0] instanceof MemoryNeuron, "not memory neuron");
        Assertions.assertFalse(cloned.layers[0].neurons[0] instanceof ForgetNeuron, "not forget neuron");
        Assertions.assertTrue(cloned.layers[0].neurons[1] instanceof MemoryNeuron, "memory neuron");
        Assertions.assertTrue(cloned.layers[0].neurons[2] instanceof ForgetNeuron, "forget neuron");
        Assertions.assertEquals(1, cloned.layers[0].memoryNeurons.size(), "tracked memory neuron list size");
        Assertions.assertEquals("1", cloned.metadata.getProperty("test"), "metadata");

        //vary the clone from original
        //first neuron
        cloned.layers[0].neurons[0].weights[0] = -1;
        cloned.layers[0].neurons[0].weights[1] = -1;
        //memory neuron
        cloned.layers[0].neurons[1].weights[0] = -1;
        cloned.layers[0].neurons[1].weights[1] = -1;
        memoryNeuron = (MemoryNeuron)cloned.layers[0].neurons[1];
        memoryNeuron.memoryUnit.memory[0] = -1;
        //forget neuron
        cloned.layers[0].neurons[2].weights[0] = -1;
        cloned.layers[0].neurons[2].weights[1] = -1;


        //assert references were broken
        //first neuron
        Assertions.assertEquals(1, original.layers[0].neurons[0].weights[0], "original n0w0");
        Assertions.assertEquals(1, original.layers[0].neurons[0].weights[1], "original n0w1");
        //memory neuron
        Assertions.assertEquals(1, original.layers[0].neurons[1].weights[0], "original m0w0");
        Assertions.assertEquals(1, original.layers[0].neurons[1].weights[1], "original m0w1");
        memoryNeuron = (MemoryNeuron)original.layers[0].neurons[1];
        Assertions.assertEquals(1, memoryNeuron.memoryUnit.memory[0], "original mu m0");
        //forget neuron
        Assertions.assertEquals(1, original.layers[0].neurons[2].weights[0], "orignal f0w0");
        Assertions.assertEquals(1, original.layers[0].neurons[2].weights[1], "original f0w1");
        forgetNeuron = (ForgetNeuron) original.layers[0].neurons[2];
        Assertions.assertEquals(1, forgetNeuron.memoryUnit.memory[0], "original mu f0");
    }
}
