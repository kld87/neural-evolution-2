import java.util.Properties;

public class Network {
    public Layer[] layers;
    public Properties metadata = new Properties();

    public Network(Layer[] layers) {
        this.layers = layers;
    }

    public double[] tick(double[] inputs) {
        for (int i = 0; i < layers.length; i++) {
            inputs = layers[i].tick(inputs);
        }
        return inputs;
    }
}
