import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.Arrays;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Preprocess {

	// byte[] to Image
	public static Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	// Int[] to byte[]
	public static byte[] toByteArray(int[] intArray) {
		byte[] bytes = new byte[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			bytes[i] = (byte) intArray[i];
		}
		return bytes;
	}

	// Double[] to byte[]
	public static byte[] toByteArray(double[] intArray) {
		byte[] bytes = new byte[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			bytes[i] = (byte) intArray[i];
		}
		return bytes;
	}

	// Mat to double[]
	public static double[] toDoubleArray(Mat m) {
		int init = 0;
		double[] darray = new double[m.rows() * m.cols()];
		for (int i = 0; i < m.rows(); i++) {
			for (int j = 0; j < m.cols(); j++) {
				double temp;
				if (m.get(i, j)[0] != 0.0)
					temp = 1.0;
				else
					temp = 0.0;
				darray[init++] = temp;
				// darray[init++] = m.get(i, j)[0] / 255.0;
			}
		}
		System.out.println("HIHI: " + Arrays.toString(darray));
		return darray;
	}

	public static Mat loadImageByte(String fileName) {
		Mat rgbLoadedImage = null;
		File fileName1 = new File(fileName);
		System.out.println(fileName1.getAbsolutePath());
		Mat learningImage = Highgui.imread(fileName1.getAbsolutePath(), Imgproc.COLOR_BGR2GRAY);

		if (learningImage.width() > 0) {
			rgbLoadedImage = new Mat(learningImage.size(), learningImage.type());
			Imgproc.cvtColor(learningImage, rgbLoadedImage, Imgproc.COLOR_BGR2GRAY, 0);
			learningImage.release();
			learningImage = null;
		}
		System.out.println("Type:" + rgbLoadedImage.type());
		return rgbLoadedImage;
	}

	public static double[] imageToDouble(String fileName) {
		return toDoubleArray(loadImageByte(fileName));
	}

	// Double[] to Mat, and then Mat to image
	public static Image doubleToImage(double[] m) {
		int len = (int) Math.sqrt(m.length);
		Mat byteMat = new Mat(len, len, CvType.CV_8U);
		byteMat.put(0, 0, toByteArray(m));
		return toBufferedImage(byteMat);
	}
	
	/*
	 * //Mat is outdated // 2d to 1d reshape public Mat reshape(String fileName)
	 * { return loadImageByte(fileName).reshape(1, 1); }
	 * 
	 * // Image to 1d double[] public Mat loadImageFloat(String fileName) {
	 * return floatMat(reshape(fileName)); }
	 * 
	 * public Mat floatMat(Mat origin) { Mat floatMat = new Mat();
	 * origin.convertTo(floatMat, CvType.CV_32F, 1 / 255.0); return floatMat; }
	 * 
	 * public Mat floatMatOutput(Mat origin) { Mat floatMat = new Mat();
	 * origin.convertTo(floatMat, CvType.CV_32F); return floatMat; }
	 * 
	 * public Mat createMat(int number) { int row = 0, col = 0; int data[] = {
	 * number }; Mat output = new Mat(1, 1, CvType.CV_32S); output.put(row, col,
	 * data); return floatMatOutput(output); }
	 */
}
