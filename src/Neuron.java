public class Neuron {

    public double[] weights;

    public Neuron(double[] weights) {
        this.weights = weights.clone();
    }

    public double tick(double[] inputs) {
        double sum = 0.0;

        for (int i = 0; i < inputs.length; i++) {
            sum += inputs[i] * weights[i];
        }

        //bias
        sum += 1 * weights[weights.length-1];

        //instead of doing >= 1 we do the below re: float fuzziness
        return sum > 0.999999999 ? 1 : 0;
    }
}
