package comm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import data.DataCollection;
import data.SensorData;
import data.SensorDataSet;

public class CommThread implements Runnable {
	
	public static final String HOSTNAME = "192.168.1.1";
	public static final int PORT = 42880;
	
	public static final double INCOMING_DATA_COEFFICIENT = 3.3;
	
	private RobotComm comm;

	@Override
	public void run() {
		
		comm = new RobotComm(HOSTNAME, PORT);
		comm.open();
		//Scanner for input from the console
		InputStreamReader console = new InputStreamReader(System.in);
		
		while(true) {
			
			//If the robot has sent us any messages
			if(comm.hasInput()) {
				
				String line = comm.getLine();
				//If the message is the beginning of sweep data
				if(line.equals("/s")) {
					readSweepData();
				}
				//If the robot is moving forward/backwards
				if(line.equals("/8") || line.equals("/2") || line.equals("/e")) {
					readMovementData();
				}
				//If the robot is turning in place
				if(line.equals("/4") || line.equals("/6")) {
					readTurningData();
				}
			}
			
			try {
				//If we have input from console
				if(console.ready()) {
					
					//Grab that char input
					char command = (char) console.read();
					if(Character.isWhitespace(command))
						continue;
					
					//Reset the connection  to the robot
					if(command == '!') {
						comm.close();
						comm = new RobotComm(HOSTNAME, PORT);
						comm.open();
					//Clear the pillars
					} else if(command == '#') {
						DataCollection.clearPillars();
						DataCollection.newData = true;
					//Clear the cliff sensor data
					} else if(command == '$') {
						DataCollection.clearCliff();
						DataCollection.newData = true;
					//Give up! Quit! Just stop trying!
					} else if(command == 'q') {
						comm.close();
						break;
					} else {
						//Send the command to the robot
						System.out.println("Sending command: " + command);
						comm.sendLine(String.valueOf(command));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			console.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSweepData() {
		
		SensorDataSet sds = new SensorDataSet();
		DataCollection.addData(sds);
		DataCollection.partyTime = true;
		
		while(true) {
			Scanner lineScan = new Scanner(comm.getLine());
			if(lineScan.hasNext()) {
				String command = lineScan.next();
				if(command.equals("/d")) {
					sds.add(new SensorData(
							lineScan.nextInt(),
							lineScan.nextInt(),
							lineScan.nextInt()));
				}
				if(command.equals("/~")) {
					lineScan.close();
					break;
				}
			}
			lineScan.close();
		}
		
		DataCollection.addPillars(sds);
		DataCollection.newData = true;
		DataCollection.partyTime = false;
	}
	
	public void readMovementData() {
		
		DataCollection.partyTime = true;
		
		while(true) {
			Scanner lineScan = new Scanner(comm.getLine());
			if(lineScan.hasNext()) {
				String command = lineScan.next();
				if(command.equals("/d")) {
					DataCollection.move(lineScan.nextDouble() / INCOMING_DATA_COEFFICIENT);
					DataCollection.newData = true;
				}
				if(command.equals("/e")) {
					if(lineScan.hasNextInt()) {
						DataCollection.addCliff(lineScan.nextInt());
					}
				}
				if(command.equals("/~")) {
					lineScan.close();
					break;
				}
			}
			lineScan.close();
		}
		
		DataCollection.partyTime = false;
	}
	
	public void readTurningData() {
		
		DataCollection.partyTime = true;
		
		while(true) {
			Scanner lineScan = new Scanner(comm.getLine());
			if(lineScan.hasNext()) {
				String command = lineScan.next();
				if(command.equals("/d")) {
					DataCollection.turn(-lineScan.nextDouble());
					DataCollection.newData = true;
				}
				if(command.equals("/~")) {
					lineScan.close();
					break;
				}
			}
			lineScan.close();
		}
		
		DataCollection.partyTime = false;
	}
}
