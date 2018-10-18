package data;

import java.awt.Color;
import java.awt.Graphics;

import geometery.Point;
import ui.RobotPanel;

public class MovingPoint {
	
	public Point position;
	
	public MovingPoint(Point position) {
		this.position = position;
	}
	
	public void shift(double x, double y) {
		position = position.add(x, y);
	}
	
	public void shiftP(double degrees) {
		position = position.toPolar().shift(degrees, 0).toPoint();
	}
	
	public void draw(Graphics g, Color c) {
		g.setColor(c);
		Point draw = position
				.multiply(1, -1)
				.multiply(DataCollection.CM_TO_PIXELS)
				.add(RobotPanel.ORIGIN_X, RobotPanel.ORIGIN_Y)
				.add(-5);
		g.fillOval((int) draw.x, (int) draw.y, 10, 10);
	}
}
