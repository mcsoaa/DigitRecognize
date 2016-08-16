import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import au.com.bytecode.opencsv.CSVReader;

public class Main {
	public final int LOOPNO = 10;
	public Net NNet;
	boolean DEBUG = true;
	JPanel gui;
	JLabel imageCanvas;
	JLabel textCanvas;
	static int imageCounter = 0;
	Timer timer = null;
	TrainingData testData;
	TrainingData trainData;

	public static void main(String[] args) {
		new Main().combineTest();
	}

	public void combineTest() {
		initNet(196, 1000, 4, 0.001, true); // The origin is 25 hidder layer
		//initData();
		//trainBigData();
		//predict1kData();
		//NNet.saveNet();
		showUI("1000digit.csv");
	}

	public void trainBigData() {
		int iteration = 0;
		double[][] mdata = trainData.getData();
		double[][] label = trainData.getLabel();
		for (int j = 0; j < LOOPNO; j++) {
			long start = System.currentTimeMillis();
			for (int i = 0; i < mdata.length; i++) {
				trainNet(mdata[i], label[i]);
				iteration++;
			}
			long elapsedTime = System.currentTimeMillis() - start;
			System.out.println("Used Time: " + elapsedTime / 1000F + " Iteration: " + iteration);
			predict1kData();
		}
	}

	public double predict1kData() {

		int correctNo = 0;
		double[][] mdata = testData.getData();
		double[][] label = testData.getLabel();
		double[] resultLabel = new double[label.length];
		for (int i = 0; i < label.length; i++) {
			resultLabel[i] = calculateOutputValue(label[i]);
		}

		for (int i = 0; i < resultLabel.length; i++) {
			double[] result = predictNet(mdata[i]);
			if (calculateOutputValue(result) == resultLabel[i]) {
				correctNo++;
			}
		}
		System.out.println("Correct Number: " + correctNo);
		return 1.0 - (correctNo / resultLabel.length);
	}

	public void showUI(String fileName) {

		TrainingData tdata = getCSVData(fileName);
		double[][] showData = tdata.getData();
		tdata.normalize();
		tdata.DS();
		double[][] mdata = tdata.getData();
		double[][] label = tdata.getLabel();
		double[] resultLabel = new double[label.length];
		for (int i = 0; i < label.length; i++) {
			resultLabel[i] = calculateOutputValue(label[i]);
		}

		Runnable r = new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Image Viewer");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				final Main viewer = new Main();
				f.setContentPane(viewer.getGui());
				f.setSize(600, 700);
				f.setLocationByPlatform(true);
				f.setVisible(true);
				ActionListener actionListener = new ActionListener() {

					public void actionPerformed(ActionEvent actionEvent) {

						double[] result = predictNet(mdata[imageCounter]);
						// Problem HERE
						viewer.setImage(Preprocess.doubleToImage(showData[imageCounter]),
								String.valueOf(calculateOutputValue(result)));
						imageCounter++;
						if (imageCounter == 999) {
							timer.stop();
						}
					}
				};
				timer = new Timer(500, actionListener); // 0.5secs
				timer.start(); // readable time 400ms
			}
		};
		SwingUtilities.invokeLater(r);
	}

	// Load real image Testing
	public void test3() {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		initNet(400, 25, 2, 0.0001, false);
		Preprocess pre_image = new Preprocess();
		double[] bye1 = Preprocess.toDoubleArray(Preprocess.loadImageByte("icon.png"));
		double[] bye2 = Preprocess.toDoubleArray(Preprocess.loadImageByte("icon2.png"));
		double[] bye3 = Preprocess.toDoubleArray(Preprocess.loadImageByte("icon3.png"));
		double[] bye4 = Preprocess.toDoubleArray(Preprocess.loadImageByte("icon4.png"));
		double[] bye5 = Preprocess.toDoubleArray(Preprocess.loadImageByte("icon5.png"));
		double[] bye6 = Preprocess.toDoubleArray(Preprocess.loadImageByte("icon6.png"));

		double[][] output = { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } };

		for (int i = 0; i < 100000; i++) {
			trainNet(bye1, output[0]);
			trainNet(bye2, output[1]);
			trainNet(bye3, output[2]);
			trainNet(bye4, output[3]);
		}
		predictNet(bye1);
		predictNet(bye2);
		predictNet(bye3);
		predictNet(bye4);
		predictNet(bye5);
		predictNet(bye6);
	}

	public static double calculateOutputValue(double[] input) {
		return input[0] * 8 + input[1] * 4 + input[2] * 2 + input[3];
	}

	//Data part
	public static TrainingData getCSVData(String filename) {
		TrainingData tdata = new TrainingData();
		try {
			System.out.println("Start reading data....");
			tdata = Main.readCSV(filename, ',', 1);
			System.out.println("Finished reading " + tdata.getData().length + "...");
		} catch (Exception e) {
			System.out.println(e.getStackTrace()[0] + "  " + e.getStackTrace()[1] + "12312312312");
		}
		return tdata;
	}

	public static TrainingData readCSV(String filename, char separator, int headerLines) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		CSVReader cr = new CSVReader(br, separator, '\"', '\\', headerLines);
		List<String[]> values = cr.readAll();
		cr.close();
		br.close();

		int numRows = values.size();
		int numCols = values.get(0).length;
		double[][] data = new double[numRows][numCols - 1];
		double[] lableData = new double[numRows];
		// 784 pixels.
		for (int row = 0; row < numRows; row++) {
			String[] rowValues = values.get(row);
			for (int col = 0; col < numCols; col++) {
				Double v = Double.parseDouble(rowValues[col]);
				if (col == 0) {
					lableData[row] = v;
				} else {

					data[row][col - 1] = v;
				}
			}
			// System.out.println("HIHI: " + Arrays.toString(data[row]));
			// System.out.println("HIHI: " + data[row].length);
		}

		return new TrainingData(lableData, data);
	}

	public void initData() {
		testData = getCSVData("1000digit.csv");
		testData.normalize();
		testData.DS();

		trainData = getCSVData("train.csv");
		trainData.normalize();
		trainData.DS();
	}

	//Net part
	public void initNet(int inputLayer, int hiddenLayer, int outputLayer, double eta, boolean usedOld) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		if (usedOld)
			NNet = new Net();
		else
			NNet = new Net(inputLayer, hiddenLayer, outputLayer, eta);
	}

	// One time array train.
	public void trainNet(double[] inputArray, double[] outputArray) {
		NNet.feedForward(inputArray);
		// System.out.println("Target: " + Arrays.toString(outputArray));
		// NNet.getResults();
		NNet.backProp(outputArray);
	}

	// One time array predict
	public double[] predictNet(double[] inputArray) {
		NNet.feedForward(inputArray);
		return NNet.getResults();
	}

	// Gui Part
	public void setImage(Image image, String text) {
		imageCanvas.setIcon(new ImageIcon(image));
		textCanvas.setText(text);
	}

	public void initComponents() {
		if (gui == null) {
			gui = new JPanel(new BorderLayout());
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));
			imageCanvas = new JLabel();
			textCanvas = new JLabel();
			JPanel imageCenter = new JPanel(new GridBagLayout());
			imageCenter.add(imageCanvas);
			imageCenter.add(textCanvas);
			JScrollPane imageScroll = new JScrollPane(imageCenter);
			imageScroll.setPreferredSize(new Dimension(300, 100));
			gui.add(imageScroll, BorderLayout.CENTER);
		}
	}

	public Container getGui() {
		initComponents();
		return gui;
	}
}
