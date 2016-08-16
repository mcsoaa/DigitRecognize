import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.ml.CvANN_MLP;
import org.opencv.ml.CvANN_MLP_TrainParams;

public class ImageTrain {
	private CvANN_MLP bp = new CvANN_MLP();
	CvANN_MLP_TrainParams params = new CvANN_MLP_TrainParams();

	public ImageTrain() {
		params.set_train_method(1);
		int row = 0, col = 0;
		int data[] = { 400, 100, 1 };
		Mat layerSizes = new Mat(1, 3, CvType.CV_32S);
		layerSizes.put(row, col, data);
		bp.create(layerSizes, CvANN_MLP.SIGMOID_SYM, 0.5, 0.5);
	}

	public void imageTrain(Mat input, Mat output) {
		Mat weight = new Mat();
		bp.train(input, output, weight, weight, params, CvANN_MLP.UPDATE_WEIGHTS);
		//bp.train(input, output, weight);
		System.out.println(weight.dump());
	}

	public Mat ImagePredict(Mat input) {
		Mat result = new Mat();
		bp.predict(input, result);
		System.out.println("Predict: " + result.dump());
		return result;
	}

}
