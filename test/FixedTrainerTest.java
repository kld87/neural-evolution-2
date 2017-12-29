import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FixedTrainerTest {
    @Test
    void testTrainer() {
        FixedTrainer t = new FixedTrainer(4, 4);

        //we're going to use the same testing approach as DynamicTrainerTest re: randomness
        Network n = new Network(new Layer[]{
                new Layer(new Neuron[]{
                        new Neuron(new double[2]),
                        new Neuron(new double[2]),
                })
        });
        int c;

        for (int i = -4; i<= 4; i++) {
            c = 0;
            while (c < 1000) {
                t.mutateNetwork(n);
                if (n.layers[0].neurons[0].weights[0] == (double)i/4) break;
                c++;
            }
            Assertions.assertTrue(c < 1000, "first neuron first weight from step " + i);
        }

        for (int i = -4; i<= 4; i++) {
            c = 0;
            while (c < 1000) {
                t.mutateNetwork(n);
                if (n.layers[0].neurons[1].weights[1] == (double)i/4) break;
                c++;
            }
            Assertions.assertTrue(c < 1000, "second neuron second weight from step " + i);
        }
    }
}
