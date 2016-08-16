import java.io.Serializable;

public class Connection implements Serializable {
	public double weight;

	public Connection() {
		weight = 0.0;
	}

	public Connection(double temp) {
		weight = temp;
	}
}
