package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import data.DataCollection;

public class RobotPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static final int W_WIDTH = 800;
	public static final int W_HEIGHT = 800;
	public static final int ORIGIN_X = W_WIDTH / 2;
	public static final int ORIGIN_Y = W_HEIGHT / 2;

	public RobotPanel() {
		super();
		init();
	}
	
	private void init() {
		Timer paintTimer = new Timer(25, this);
		paintTimer.start();
		
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));
		setDoubleBuffered(true);
	}
	
	protected void paintComponent(Graphics g) {
		DataCollection.draw(g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
