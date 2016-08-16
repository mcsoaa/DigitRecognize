import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("serial")
public class Neuron implements Serializable {

	private double output;
	private double etaValue;
	private double gradient;
	public int neuronIndex;
	public ArrayList<Connection> lineGroup = new ArrayList<Connection>();
	Random rand = new Random();

	Neuron(int lineNo, int index, double eta) {
		initializeLine(lineNo);
		neuronIndex = index;
		etaValue = eta;
	}

	// assign each line a random weight
	void initializeLine(int lineNo) {
		for (int i = 0; i < lineNo; i++) {
			double randomValue = -0.6 + (0.6 - (-0.6)) * rand.nextDouble();
			lineGroup.add(new Connection(randomValue));
		}
	}

	// the weighted(output weights) sum of s from its next layer.
	double sumProduct(ArrayList<Neuron> nextLayer) {
		double sum = 0.0;
		for (int i = 0; i < nextLayer.size(); i++) {
			sum += (lineGroup.get(i).weight * nextLayer.get(i).gradient);
		}
		return sum;
	}

	// Update weight
	// First compute delta weight by (rate * previous layer output * this neuron
	// gradient)
	// Then update this neuron weight by adding delta weight
	void updateWeight(ArrayList<Neuron> prevLayer) {
		for (int i = 0; i < prevLayer.size(); i++) {
			double deltaWeight = etaValue * prevLayer.get(i).getOutput() * gradient;
			prevLayer.get(i).lineGroup.get(neuronIndex).weight += deltaWeight;
		}
	}

	// Sigmoid activation function
	public static double sigmoid(double x) {
		return (double) (1 / (1 + Math.exp(-x)));
	}

	void setOutput(double temp) {
		output = temp;
	}

	double getOutput() {
		return output;
	}

	void setGradient(double temp) {
		gradient = temp;
	}

	double getGradient() {
		return gradient;
	}
}
