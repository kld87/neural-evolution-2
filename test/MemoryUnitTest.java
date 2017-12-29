import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class MemoryUnitTest {
    @Test
    void testMemoryUnit() {
        //test init
        MemoryUnit mu = new MemoryUnit(2);
        Assertions.assertArrayEquals(new double[]{0, 0}, mu.memory, "starts empty");

        //test storing
        double[] data = new double[]{1, 1};
        mu.store(data);
        data[0] = 0;
        Assertions.assertArrayEquals(new double[]{1, 1}, mu.memory, "stored and broke reference");

        //test clearing
        mu.forget();
        Assertions.assertArrayEquals(new double[]{0, 0}, mu.memory, "forgot");
    }
}