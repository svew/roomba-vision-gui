package data;

import java.awt.Color;
import java.awt.Graphics;

import geometery.Point;
import ui.RobotPanel;

class Pillar extends MovingPoint {
	
	public double radius;
	public int age;
	
	public Pillar(Point position, double radius) {
		super(position);
		this.radius = radius;
	}
	
	public void draw(Graphics g, Color c) {
		Point screenPos = position
				.multiply(1,-1)
				.add(-radius)
				.multiply(DataCollection.CM_TO_PIXELS)
				.add(RobotPanel.ORIGIN_X, RobotPanel.ORIGIN_Y);
		Point screenSize = new Point(radius, radius)
				.multiply(2)
				.multiply(DataCollection.CM_TO_PIXELS);
		g.setColor(Color.RED);
		g.drawOval(
				(int) screenPos.x, 
				(int) screenPos.y, 
				(int) screenSize.x, 
				(int) screenSize.y);
	}
}