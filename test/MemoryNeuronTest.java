import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class MemoryNeuronTest {
    @Test
    void tickTest() {
        MemoryUnit mu = new MemoryUnit(2);
        MemoryNeuron n = new MemoryNeuron(new double[]{1, 1, 0}, mu);

        //trigger store, check memory
        n.tick(new double[]{0, 1});
        Assertions.assertArrayEquals(new double[]{0, 1}, mu.memory, "memory set");
        //don't trigger store, check memory
        n.tick(new double[]{0, 0});
        Assertions.assertArrayEquals(new double[]{0, 1}, mu.memory, "memory persists");
    }
}