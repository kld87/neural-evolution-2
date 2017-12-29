import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class NetworkTest {
    @Test
    void tickTest() {
        //basic test, two layers that will flip inputs
        Layer l1 = new Layer(new Neuron[]{
                new Neuron(new double[]{1, 0, 0}),
                new Neuron(new double[]{0, 1, 0})
        });
        Layer l2 = new Layer(new Neuron[]{
                new Neuron(new double[]{0, 1, 0}),
                new Neuron(new double[]{1, 0, 0})
        });
        Network n = new Network(new Layer[]{l1, l2});

        Assertions.assertArrayEquals(new double[]{0, 1}, n.tick(new double[]{1, 0}), "flip 1");
        Assertions.assertArrayEquals(new double[]{1, 0}, n.tick(new double[]{0, 1}), "flip 2");

        //memory test
        //we will have two layers, first w/ memory/forget neurons, second w/ 4 regular neurons
        //latter two inputs into l2 should be the memory from l1
        MemoryUnit mu = new MemoryUnit(2);
        l1 = new Layer(new Neuron[]{
                new MemoryNeuron(new double[]{1, 0, 0}, mu),
                new ForgetNeuron(new double[]{0, 1, 0}, mu)
        });
        l2 = new Layer(new Neuron[]{
                new Neuron(new double[]{1, 0, 0, 0, 0}),
                new Neuron(new double[]{0, 1, 0, 0, 0}),
                new Neuron(new double[]{0, 0, 1, 0, 0}),
                new Neuron(new double[]{0, 0, 0, 1, 0})
        });
        n = new Network(new Layer[]{l1, l2});
        Assertions.assertArrayEquals(new double[]{0, 0, 0, 0}, n.tick(new double[]{0, 0}), "do nothing");
        Assertions.assertArrayEquals(new double[]{1, 0, 1, 0}, n.tick(new double[]{1, 0}), "memorize");
        Assertions.assertArrayEquals(new double[]{0, 0, 1, 0}, n.tick(new double[]{0, 0}), "remember");
        Assertions.assertArrayEquals(new double[]{0, 1, 0, 0}, n.tick(new double[]{0, 1}), "forget");
        Assertions.assertArrayEquals(new double[]{1, 1, 0, 0}, n.tick(new double[]{1, 1}), "memorize & forget");
    }
}