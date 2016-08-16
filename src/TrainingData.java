public class TrainingData {
	private double[][] data;
	private double[][] lableData;

	public TrainingData() {

	}

	public TrainingData(double[] lable, double[][] mdata) {
		setData(lable, mdata);
	}

	public void normalize() {
		data = normalize(data);
	}

	public void DS() {
		data = downSampling2(data);
	}

	public void setData(double[] lable, double[][] mdata) {
		data = mdata;
		setLabel(lable);
	}

	public void setLabel(double[] lable) {
		lableData = new double[lable.length][4];
		for (int i = 0; i < lable.length; i++) {
			switch ((int) lable[i]) {
			case 0:
				lableData[i] = new double[] { 0.0, 0.0, 0.0, 0.0 };
				break;
			case 1:
				lableData[i] = new double[] { 0.0, 0.0, 0.0, 1.0 };
				break;
			case 2:
				lableData[i] = new double[] { 0.0, 0.0, 1.0, 0.0 };
				break;
			case 3:
				lableData[i] = new double[] { 0.0, 0.0, 1.0, 1.0 };
				break;
			case 4:
				lableData[i] = new double[] { 0.0, 1.0, 0.0, 0.0 };
				break;
			case 5:
				lableData[i] = new double[] { 0.0, 1.0, 0.0, 1.0 };
				break;
			case 6:
				lableData[i] = new double[] { 0.0, 1.0, 1.0, 0.0 };
				break;
			case 7:
				lableData[i] = new double[] { 0.0, 1.0, 1.0, 1.0 };
				break;
			case 8:
				lableData[i] = new double[] { 1.0, 0.0, 0.0, 0.0 };
				break;
			case 9:
				lableData[i] = new double[] { 1.0, 0.0, 0.0, 1.0 };
				break;
			}
		}
	}

	public double[][] getLabel() {
		return lableData;
	}

	public double[][] getData() {
		return data;
	}

	// Normalize data
	public double[][] normalize(double[][] raw) {
		double[][] darray = new double[raw.length][raw[0].length];
		for (int i = 0; i < raw.length; i++) {
			for (int j = 0; j < raw[i].length; j++) {
				double temp;
				temp = raw[i][j] / 255.0;
				darray[i][j] = temp;
			}
			// System.out.println("HIHI: " + Arrays.toString(darray[i]));
		}
		return darray;
	}

	public double[][] convertTwoD(double[] raw) {
		int counter = 0;
		int arrayLen = (int) Math.sqrt(raw.length);
		double array2d[][] = new double[arrayLen][arrayLen];
		for (int i = 0; i < arrayLen; i++)
			for (int j = 0; j < arrayLen; j++) {
				array2d[i][j] = raw[counter++];
			}
		return array2d;
	}

	public double[] convertOneD(double[][] raw) {
		int counter = 0;
		double[] array1d = new double[raw.length * raw[0].length];
		for (int i = 0; i < raw.length; i++)
			for (int j = 0; j < raw[i].length; j++) {
				array1d[counter++] = raw[i][j];
			}
		return array1d;
	}

	public double[][] downSampling(double[][] raw) {
		int counter = 0;
		double[][] downSample = new double[raw.length][(raw[0].length / 4)];
		for (int i = 0; i < raw.length; i++) {
			double sum = 0;
			for (int j = 0; j < raw[i].length; j++) {
				counter++;
				if (counter % 4 != 0) {
					sum += raw[i][j];
				} else {
					downSample[i][(counter / 4) - 1] = sum / 4;
					sum = 0;
				}
			}
			counter = 0;
		}

		return downSample;
	}

	public double[][] downSampling2(double[][] raw) {
		double[][] result = new double[raw.length][raw[0].length / 4];

		for (int i = 0; i < raw.length; i++) {
			double[][] matrix = convertTwoD(raw[i]);
			// System.out.println("Print: ");
			// TrainingData.printMatrix(matrix);
			int counter = 0;

			for (int j = 0; j < matrix.length; j += 2) {
				double sum = 0;
				for (int k = 0; k < matrix[j].length; k += 2) {
					sum = matrix[j][k] + matrix[j][k + 1] + matrix[j + 1][k] + matrix[j + 1][k + 1];
					result[i][counter++] = sum / 4;
				}
			}

		}
		return result;
	}

	public static void printMatrix(double[][] grid) {
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				System.out.print(grid[r][c] + " ");
			}
			System.out.println();
		}
	}

}
