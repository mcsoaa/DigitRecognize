import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Net {
	ArrayList<ArrayList<Neuron>> multiLayer = new ArrayList<ArrayList<Neuron>>();

	public Net() {
		loadNet();
	}

	public Net(int inputLayer, int hiddenLayer, int outputLayer, double eta) {
		initial();
		for (int i = 0; i < inputLayer; i++) {
			multiLayer.get(0).add(new Neuron(hiddenLayer, i, eta));
		}
		for (int i = 0; i < hiddenLayer; i++) {
			multiLayer.get(1).add(new Neuron(outputLayer, i, eta));
		}
		for (int i = 0; i < outputLayer; i++) {
			multiLayer.get(2).add(new Neuron(0, i, eta));
		}
	}

	public void initial() {
		multiLayer.add(new ArrayList<Neuron>());
		multiLayer.add(new ArrayList<Neuron>());
		multiLayer.add(new ArrayList<Neuron>());
	}

	public void feedForward(double[] inputVals) {

		// initialize all the neurons in input layer
		for (int i = 0; i < inputVals.length; i++) {
			multiLayer.get(0).get(i).setOutput(inputVals[i]);
		}

		// feed forward from the previous layer neurons
		for (int i = 1; i < multiLayer.size(); i++) {
			ArrayList<Neuron> prevLayer = multiLayer.get(i - 1);
			for (int j = 0; j < multiLayer.get(i).size(); j++) {
				// weighted sum of (output of previous neuron * weight of
				// previous neuron)
				// Pass to sigmoid function and generate new output
				double temp = 0.0;
				for (int z = 0; z < prevLayer.size(); z++) {
					double prevLineValue = prevLayer.get(z).getOutput()
							* prevLayer.get(z).lineGroup.get(multiLayer.get(i).get(j).neuronIndex).weight;
					temp += prevLineValue;
				}
				multiLayer.get(i).get(j).setOutput(Neuron.sigmoid(temp));
			}
		}
	}

	public void backProp(double[] targetVals) {
		ArrayList<Neuron> lastLayer = multiLayer.get(multiLayer.size() - 1);

		// calculate gradients of output layer
		for (int i = 0; i < lastLayer.size(); i++) {
			double output = lastLayer.get(i).getOutput();
			// o * (1-o) * (t - o)
			double gradientValue = output * (1 - output) * (targetVals[i] - output);
			lastLayer.get(i).setGradient(gradientValue);
		}

		// calculate gradients of hidden layer
		for (int i = multiLayer.size() - 2; i > 0; i--) {
			ArrayList<Neuron> hiddenLayer = multiLayer.get(i);
			ArrayList<Neuron> nextLayer = multiLayer.get(i + 1);

			for (int j = 0; j < hiddenLayer.size(); j++) {
				double output = hiddenLayer.get(j).getOutput();
				// o * (1-o) * weighted sum of gradient from its next layer
				double gradientValue = output * (1 - output) * hiddenLayer.get(j).sumProduct(nextLayer);
				hiddenLayer.get(j).setGradient(gradientValue);
			}
		}

		// Update Weights (input layer not modified)
		for (int i = multiLayer.size() - 1; i > 0; i--) {
			ArrayList<Neuron> layer = multiLayer.get(i);
			ArrayList<Neuron> prevLayer = multiLayer.get(i - 1);

			for (int j = 0; j < layer.size(); j++) {
				layer.get(j).updateWeight(prevLayer);
			}
		}
	}

	public double[] getResults() {
		// System.out.print("Output: ");
		double[] result = new double[multiLayer.get(2).size()];
		for (int i = 0; i < multiLayer.get(2).size(); i++) {
			result[i] = multiLayer.get(2).get(i).getOutput() > 0.6 ? 1.0 : 0.0;
			// System.out.print(result[i] + " ");
		}
		// System.out.println();
		return result;
	}

	public void writeFile(String fileName, ArrayList<Neuron> layer) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(layer);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error occur in write!");
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Neuron> readFile(String fileName) {
		ArrayList<Neuron> layer = null;
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			layer = (ArrayList<Neuron>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			System.out.println("Error occur in read!");
		}
		return layer;
	}

	public void saveNet() {
		writeFile("input.txt", multiLayer.get(0));
		writeFile("hidden.txt", multiLayer.get(1));
		writeFile("output.txt", multiLayer.get(2));
	}

	public void loadNet() {
		multiLayer.add(readFile("input.txt"));
		multiLayer.add(readFile("hidden.txt"));
		multiLayer.add(readFile("output.txt"));
	}
}
