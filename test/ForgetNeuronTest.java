import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class ForgetNeuronTest {
    @Test
    void tickTest() {
        MemoryUnit mu = new MemoryUnit(2);
        MemoryNeuron mn = new MemoryNeuron(new double[]{1, 1, 0}, mu);
        ForgetNeuron fn = new ForgetNeuron(new double[]{1, 1, 0}, mu);

        //trigger store, check memory
        mn.tick(new double[]{0, 1});
        Assertions.assertArrayEquals(new double[]{0, 1}, mu.memory, "memory set");
        //trigger nothing, check memory
        fn.tick(new double[]{0, 0});
        Assertions.assertArrayEquals(new double[]{0, 1}, mu.memory, "memory persists");
        //trigger forget, check memory
        fn.tick(new double[]{0, 1});
        Assertions.assertArrayEquals(new double[]{0, 0}, mu.memory, "forgot");
        //trigger memory & forget, check memory
        fn.tick(new double[]{1, 1});
        Assertions.assertArrayEquals(new double[]{0, 0}, mu.memory, "didn't memorize");
    }
}