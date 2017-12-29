import org.junit.jupiter.api.Test;
        import org.junit.jupiter.api.Assertions;

class NeuronTest {
    @Test
    void tickTest() {
        //unbiased
        Neuron n = new Neuron(new double[]{0.5, 0.5, 0});
        Assertions.assertEquals(0, n.tick(new double[]{0, 0}), "doesn't fire (0 + 0 < 1");
        Assertions.assertEquals(1, n.tick(new double[]{1, 1}), "fires (0.5 + 0.5 = 1)");
        Assertions.assertEquals(0, n.tick(new double[]{1, 0}), "does't fire (0.5 + 0 < 1)");

        //biased
        n = new Neuron(new double[]{0.5, 0.5});
        Assertions.assertEquals(0, n.tick(new double[]{0}), "doesn't fire (0 + 0.5 < 1)");
        Assertions.assertEquals(1, n.tick(new double[]{1}), "fires (0.5 + 0.5 = 1)");
    }
}