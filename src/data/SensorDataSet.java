package data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import geometery.Point;
import geometery.Polar;
import ui.RobotPanel;

public class SensorDataSet extends ArrayList<SensorData> {
	
	private static final long serialVersionUID = 1L;
	private static final Color COLOR_IR = new Color(100, 100, 255, 100);
	private static final Color COLOR_PING = new Color(100, 255, 100, 100);
	private static final Color COLOR_IR_INVALID = new Color(100, 100, 255, 20);
	private static final Color COLOR_PING_INVALID = new Color(100, 255, 100, 20);
	
	public SensorDataSet(ArrayList<SensorData> polarData) {
		super(polarData);
	}
	
	public SensorDataSet() {
		super();
	}
	
	@Override
	public synchronized boolean add(SensorData data) {
		
		//Filter data if necessary
		//The physical sensor is offset 4 cm from axis of rotation
		data.ir += 4;
		data.ping += 4;
		if(data.ir > DataCollection.DISTANCE_CUTOFF)
			data.isValid = false;
		if(data.ping > DataCollection.DISTANCE_CUTOFF)
			data.isValid = false;
		
		return super.add(data);
	}
	
	public void drawPolar(Graphics g) {
		
		Polygon ir_poly = new Polygon();
		Polygon ping_poly = new Polygon();
		
		Point center = new Point(0,0)
				.add(0, -DataCollection.CENTER_TO_AXIS)
				.multiply(DataCollection.CM_TO_PIXELS)
				.add(RobotPanel.ORIGIN_X, RobotPanel.ORIGIN_Y);
		
		ir_poly.addPoint((int) center.x, (int) center.y);
		ping_poly.addPoint((int) center.x, (int) center.y);		
		
		for(SensorData data : this) {
			
			/*
			
			Point ir_point = new Polar(data.degree, data.ir).toPoint();
			Point ping_point = new Polar(data.degree, data.ping).toPoint();
			ir_poly.addPoint(
					(int) ir_point.x * DataCollection.CM_TO_PIXELS + RobotPanel.ORIGIN_X, 
					(int) -ir_point.y * DataCollection.CM_TO_PIXELS + RobotPanel.ORIGIN_Y);
			ping_poly.addPoint(
					(int) ping_point.x * DataCollection.CM_TO_PIXELS + RobotPanel.ORIGIN_X, 
					(int) -ping_point.y * DataCollection.CM_TO_PIXELS + RobotPanel.ORIGIN_Y);
			*/
			
			Point ir_point;
			Point ping_point;
			
			if(data.isValid) {
				ir_point = new Polar(data.degree, data.ir).toPoint();
				ping_point = new Polar(data.degree, data.ping).toPoint();
			} else {
				ir_point = new Point(0,0);
				ping_point = new Point(0,0);
			}
			
			//Transform data, stretch it and move it to the middle
			ir_point = ir_point
					.multiply(1,-1)
					.add(0, -DataCollection.CENTER_TO_AXIS)
					.multiply(DataCollection.CM_TO_PIXELS)
					.add(RobotPanel.ORIGIN_X, RobotPanel.ORIGIN_Y);
			ping_point = ping_point
					.multiply(1,-1)
					.add(0, -DataCollection.CENTER_TO_AXIS)
					.multiply(DataCollection.CM_TO_PIXELS)
					.add(RobotPanel.ORIGIN_X, RobotPanel.ORIGIN_Y);
			
			//Negative y because positive y is down, when we want it up
			ir_poly.addPoint((int) ir_point.x, (int) ir_point.y);
			ping_poly.addPoint((int) ping_point.x, (int) ping_point.y);
		}
		
		g.setColor(COLOR_IR);
		g.fillPolygon(ir_poly);
		
		g.setColor(COLOR_PING);
		g.fillPolygon(ping_poly);
		
	}
	
	public void drawPolarUnfiltered() {
		
	}
	
	public void filterData() {
		
		//First, get rid of angles too thin
		for(int i = 0; i < size(); i++) {
			//When it finds a valid data point
			if(get(i).isValid) {
				int j;
				//Find the next invalid data point (Range of valid data is from i to k-1)
				for(j = i; j < size(); j++) {
					if(!get(j).isValid) {
						break;
					}
				}
				
				//Threshold for valid pillar is 5 degrees
				//Also, pillars which haven't started after the scan begins
				//Or ended after the scan is finished are invalid
				//AKA, Pillars that hit the edges of the scan aren't valid
				if(i == 0 || j >= size() || get(j).degree - get(i).degree < 5) {
					
					//Go back through and invalidate those data points if invalid
					for(int k = i; k < j && k < size(); k++) {
						get(k).isValid = false;
					}
				}
				
				//Jump the main iterator ahead past this data chunk
				i = j;
			}
		}
	}
}
