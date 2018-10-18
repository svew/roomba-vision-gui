package geometery;

public class Point {
	
	public final double x;
	public final double y;
	
	public Point(int x, int y) {
		this((double) x, (double) y);
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point add(double x, double y) {
		return new Point(this.x + x, this.y + y);
	}
	
	public Point add(Point p) {
		return new Point(this.x + p.x, this.y + p.y);
	}
	
	public Point add(double a) {
		return new Point(this.x + a, this.y + a);
	}
	
	public Point add(int a) {
		return add((double) a);
	}
	
	public Point multiply(int a) {
		return new Point(this.x * a, this.y * a);
	}
	
	public Point multiply(int a, int b) {
		return new Point(this.x * a, this.y * b);
	}
	
	public Polar toPolar() {
		return Polar.toPolar(this);
	}
	
	public static Point toPoint(Polar p) {
		double x = Math.cos(p.degrees / 180 * Math.PI) * p.radius;
		double y = Math.sin(p.degrees / 180 * Math.PI) * p.radius;
		return new Point(x, y);
	}
	
	@Override
	public boolean equals(Object o) {
		return 
				o != null &&
				o.getClass() == Point.class &&
				((Point) o).x == x &&
				((Point) o).y == y;
	}
}
