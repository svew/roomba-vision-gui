package data;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import geometery.Point;
import geometery.Polar;
import ui.RobotPanel;

public class DataCollection {
	public static final int CM_TO_PIXELS = 3;
	public static final int ROBOT_WIDTH_CM = 33;
	public static final int DISTANCE_CUTOFF = 80;
	public static final int CENTER_TO_AXIS = 9;

	public static final Color PILLAR_COLOR = Color.RED;
	public static final Color CLIFF_COLOR = new Color(255, 0, 255, 200); //Teal
	public static final Color WHITE_LINE_COLOR = new Color(0, 255, 255, 200); //Magenta
	public static final Color BUMPER_COLOR = new Color(50, 50, 50, 200); //Black
	public static final Color DISTOGRAM_COLOR = new Color(180, 180, 180); //grey
	
	public static boolean partyTime = false;
	public static boolean newData = false;
	
	private static Random rand = new Random();
	private static ArrayList<SensorDataSet> dataSets = new ArrayList<SensorDataSet>();
	private static ArrayList<Pillar> pillarDataSet = new ArrayList<>();
	private static ArrayList<MovingPoint> cliffDataSet = new ArrayList<>();
	private static ArrayList<MovingPoint> whiteDataSet = new ArrayList<>();
	private static ArrayList<MovingPoint> bumperDataSet = new ArrayList<>();

	public static void addData(SensorDataSet data) {
		dataSets.add(data);
		newData = true;
		
	}
	
	public static void addPillars(SensorDataSet data) {
		
		data.filterData();
		
		//Now, calculate pillars
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).isValid) {
				
				//Grab the chunk of data which is in sequence and valid
				ArrayList<SensorData> dataChunk = new ArrayList<>();
				int j;
				for(j = i; j < data.size(); j++) {
					if(!data.get(j).isValid) {
						break;
					}
					dataChunk.add(data.get(j));
				}
				
				//Get the min/max degrees
				double degree_min = dataChunk.get(0).degree;
				double degree_max = dataChunk.get(dataChunk.size() - 1).degree;
				
				//Sort that data by the ping data so we can find the median
				Collections.sort(dataChunk, new Comparator<SensorData>() {

					//This is the comparator function for the sorting algorithm
					@Override
					public int compare(SensorData o1, SensorData o2) throws NullPointerException {
						if(o1 == null || o2 == null)
							throw new NullPointerException();
						
						return o1.ping - o2.ping;
					}
				});
				
				//Do the calculations
				int median = dataChunk.get(dataChunk.size() / 2).ping; //Distance to the pillar
				double phi = (degree_max + degree_min) / 2; //The angle between pillar and robot
				double theta = (degree_max - degree_min) / 2 / 180 * Math.PI; //Half the angle of the two pillar edges
				double radius = median / ( 1/Math.sin(theta) - 1);
				Point position = new Polar(phi, median + radius)
						.toPoint()
						.add(0, CENTER_TO_AXIS);
				
				//Add new pillar
				pillarDataSet.add(new Pillar(position, radius));
				System.out.println("Found a pillar at degrees: " + phi);
				System.out.println("\tPillar width: " + radius * 2);
				System.out.println("\tPillar distance: " + median);
				
				//Jump past this pillar data
				i = j;
			}
		}
	}
	
	public static void addCliff(int cliffType) {
		
		switch(cliffType) {
		
		//Cases for bumper data
		case 0:
			bumperDataSet.add(new MovingPoint(
					new Polar(90, ROBOT_WIDTH_CM / 2).toPoint()));
			bumperDataSet.add(new MovingPoint(
					new Polar(120, ROBOT_WIDTH_CM / 2).toPoint()));
			bumperDataSet.add(new MovingPoint(
					new Polar(150, ROBOT_WIDTH_CM / 2).toPoint()));
			bumperDataSet.add(new MovingPoint(
					new Polar(180, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 1:
			bumperDataSet.add(new MovingPoint(
					new Polar(0, ROBOT_WIDTH_CM / 2).toPoint()));
			bumperDataSet.add(new MovingPoint(
					new Polar(30, ROBOT_WIDTH_CM / 2).toPoint()));
			bumperDataSet.add(new MovingPoint(
					new Polar(60, ROBOT_WIDTH_CM / 2).toPoint()));
			bumperDataSet.add(new MovingPoint(
					new Polar(90, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		
		//Cases for a hole
		case 2:
			whiteDataSet.add(new MovingPoint(
					new Polar(160, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 3:
			whiteDataSet.add(new MovingPoint(
					new Polar(100, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 4:
			whiteDataSet.add(new MovingPoint(
					new Polar(80, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 5:
			whiteDataSet.add(new MovingPoint(
					new Polar(20, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
			
		//Cases for a white line
		case 6:
			cliffDataSet.add(new MovingPoint(
					new Polar(160, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 7:
			cliffDataSet.add(new MovingPoint(
					new Polar(100, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 8:
			cliffDataSet.add(new MovingPoint(
					new Polar(80, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		case 9:
			cliffDataSet.add(new MovingPoint(
					new Polar(20, ROBOT_WIDTH_CM / 2).toPoint()));
			break;
		}
	}
	
	public static void draw(Graphics g) {
		
		//If new data has come in, wipe the screen clean
		if(newData) {
			newData = false;
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, RobotPanel.W_WIDTH, RobotPanel.W_HEIGHT);
		}
		
		//Draw the most recent sensor data
		if(!dataSets.isEmpty()) {
			dataSets.get(dataSets.size() - 1).drawPolar(g);
		}
		
		drawDistogram(g);
		
		//Draw all known pillars
		for(Pillar p : pillarDataSet) {
			p.draw(g, PILLAR_COLOR);
		}
		
		drawRobot(g);
		
		//Draw hole sensor data
		for(MovingPoint p : cliffDataSet) {
			p.draw(g, CLIFF_COLOR);
		}
		
		//Draw white line sensor data
		for(MovingPoint p : whiteDataSet) {
			p.draw(g, WHITE_LINE_COLOR);
		}

		//Drawing bumper data
		for(MovingPoint p : bumperDataSet) {
			p.draw(g, BUMPER_COLOR);
		}
		
		
	}
	
	private static void drawDistogram(Graphics g) {

		for(int i = 0; i < 10; i++) {
			g.setColor(new Color(
					DISTOGRAM_COLOR.getRed() + 5 * i,
					DISTOGRAM_COLOR.getGreen() + 5 * i,
					DISTOGRAM_COLOR.getRed() + 5 * i));
			int distance = (int)(ROBOT_WIDTH_CM/2.0 + 10 * i) * CM_TO_PIXELS ;
			g.drawOval(
					RobotPanel.ORIGIN_X - distance,
					RobotPanel.ORIGIN_Y - distance,
					distance * 2,
					distance * 2);
			//Point corner = new Point()
		}
	}
	
	private static void drawRobot(Graphics g) {
		//Set color to party mode! (When the robot is executing command, such as sweep or movement)
		if(partyTime) {
			Color color = new Color(
					rand.nextInt(256),
					rand.nextInt(256),
					rand.nextInt(256));
			g.setColor(color);
		//Draw boring uncolorful robot
		} else {
			g.setColor(Color.GRAY);
		}
		
		//Drawing the robot itself
		int width = ROBOT_WIDTH_CM * CM_TO_PIXELS;
		g.fillOval(
				RobotPanel.ORIGIN_X - width/2, 
				RobotPanel.ORIGIN_Y - width/2, 
				width, 
				width);
		g.setColor(Color.BLACK);
		g.drawOval(
				RobotPanel.ORIGIN_X - width/2, 
				RobotPanel.ORIGIN_Y - width/2, 
				width, 
				width);
	}
	
	public static void turn(double degrees) {
		for(Pillar p : pillarDataSet) {
			p.shiftP(degrees);
		}
		for(MovingPoint p : cliffDataSet) {
			p.shiftP(degrees);
		}
		for(MovingPoint p : whiteDataSet) {
			p.shiftP(degrees);
		}
		for(MovingPoint p : bumperDataSet) {
			p.shiftP(degrees);
		}
	}
	
	public static void move(double cm) {
		for(Pillar p : pillarDataSet) {
			p.shift(0, -cm / CM_TO_PIXELS);
		}
		for(MovingPoint p : cliffDataSet) {
			p.shift(0, -cm / CM_TO_PIXELS);
		}
		for(MovingPoint p : whiteDataSet) {
			p.shift(0, -cm / CM_TO_PIXELS);
		}
		for(MovingPoint p : bumperDataSet) {
			p.shift(0, -cm / CM_TO_PIXELS);
		}
	}
	
	public static void clearPillars() {
		pillarDataSet = new ArrayList<>();
		newData = true;
	}
	
	public static void clearCliff() {
		cliffDataSet = new ArrayList<>();
		newData = true;
	}
	
	public static void clearBumper() {
		bumperDataSet = new ArrayList<>();
		newData = true;
	}
}
