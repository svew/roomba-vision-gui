package data;

public class SensorData {
	public int degree = 0;
	public int ir = 0;
	public int ping = 0;
	public boolean isValid = true;
	
	public SensorData() {
		
	}
	
	public SensorData(int degree, int ir, int ping) {
		this.degree = degree;
		this.ir = ir;
		this.ping = ping;
	}
}
