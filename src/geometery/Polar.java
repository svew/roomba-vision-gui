package geometery;

public class Polar implements Comparable<Polar> {
	
	public final double degrees;
	public final double radius;
	
	public Polar(double degrees, double radius) {
		this.degrees = degrees;
		this.radius = radius;
	}
	
	public Polar shift(double degrees, double radius) {
		return new Polar(this.degrees + degrees, this.radius + radius);
	}
	
	public Point toPoint() {
		return Point.toPoint(this);
	}
	
	public static Polar toPolar(Point p) {
		double degrees = Math.atan2(p.y, p.x) * 180 / Math.PI;
		double radius = Math.sqrt(p.x * p.x + p.y * p.y);
		return new Polar(degrees, radius);
	}
	
	@Override
	public int compareTo(Polar p) {
		// TODO Auto-generated method stub
		if(p != null) {
			if(p.degrees > degrees)
				return -1;
			if(p.degrees < degrees)
				return 1;
			if(p.radius > radius)
				return -1;
			if(p.radius < radius)
				return 1;
		}
		
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return 
				o != null && 
				o.getClass() == Polar.class &&
				((Polar) o).degrees == degrees &&
				((Polar) o).radius == radius;
	}
}
