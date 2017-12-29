import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class LayerTest {
    @Test
    void tickTest() {
        //basic test
        Layer l = new Layer(new Neuron[]{
            new Neuron(new double[]{1, 0, 0}),
            new Neuron(new double[]{0, 1, 0})
        });

        Assertions.assertArrayEquals(new double[]{0, 0}, l.tick(new double[]{0, 0}), "fire nothing");
        Assertions.assertArrayEquals(new double[]{1, 0}, l.tick(new double[]{1, 0}), "fire first neuron only");
        Assertions.assertArrayEquals(new double[]{0, 1}, l.tick(new double[]{0, 1}), "fire second neuron only");
        Assertions.assertArrayEquals(new double[]{1, 1}, l.tick(new double[]{1, 1}), "fire both neurons");

        //simple memory test
        MemoryUnit mu = new MemoryUnit(2);
        l = new Layer(new Neuron[]{
                new MemoryNeuron(new double[]{1, 0, 0}, mu),
                new ForgetNeuron(new double[]{0, 1, 0}, mu)
        });
        //trigger store
        Assertions.assertArrayEquals(new double[]{1, 0, 1, 0}, l.tick(new double[]{1, 0}), "set memory");
        //trigger nothing
        Assertions.assertArrayEquals(new double[]{0, 0, 1, 0}, l.tick(new double[]{0, 0}), "remember");
        //trigger forget
        Assertions.assertArrayEquals(new double[]{0, 1, 0, 0}, l.tick(new double[]{0, 1}), "forget");

        //complex memory text
        MemoryUnit mu1 = new MemoryUnit(5);
        MemoryUnit mu2 = new MemoryUnit(5);
        l = new Layer(new Neuron[]{
                new Neuron(new double[]{1, 0, 0, 0, 0, 0}),
                new MemoryNeuron(new double[]{0, 1, 0, 0, 0, 0}, mu1),
                new ForgetNeuron(new double[]{0, 0, 1, 0, 0, 0}, mu1),
                new MemoryNeuron(new double[]{0, 0, 0, 1, 0, 0}, mu2),
                new ForgetNeuron(new double[]{0, 0, 0, 0, 1, 0}, mu2)
        });
        Assertions.assertArrayEquals(new double[]{
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0},
                l.tick(new double[]{0, 0, 0, 0, 0}), "no action, no error");
        Assertions.assertArrayEquals(new double[]{
                1, 1, 0, 0, 0,
                1, 1, 0, 0, 0,
                0, 0, 0, 0, 0},
                l.tick(new double[]{1, 1, 0, 0, 0}), "store first");
        Assertions.assertArrayEquals(new double[]{
                1, 0, 0, 0, 0,
                1, 1, 0, 0, 0,
                0, 0, 0, 0, 0},
                l.tick(new double[]{1, 0, 0, 0, 0}), "vanilla + first's memory");
        Assertions.assertArrayEquals(new double[]{
                0, 0, 0, 0, 0,
                1, 1, 0, 0, 0,
                0, 0, 0, 0, 0},
                l.tick(new double[]{0, 0, 0, 0, 0}), "first's memory only");
        Assertions.assertArrayEquals(new double[]{
                0, 0, 0, 1, 0,
                1, 1, 0, 0, 0,
                0, 0, 0, 1, 0},
                l.tick(new double[]{0, 0, 0, 1, 0}), "store second");
        Assertions.assertArrayEquals(new double[]{
                0, 0, 0, 0, 1,
                1, 1, 0, 0, 0,
                0, 0, 0, 0, 0},
                l.tick(new double[]{0, 0, 0, 0, 1}), "forget second");
        Assertions.assertArrayEquals(new double[]{
                1, 1, 1, 1, 1,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0},
                l.tick(new double[]{1, 1, 1, 1, 1}), "all inputs");
    }
}