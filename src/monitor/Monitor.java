package monitor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import medicalEquip.MeSystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Monitor {
	JLabel value0 = new JLabel("--");
	JLabel range0 = new JLabel("--",SwingConstants.CENTER);
	JLabel value1 = new JLabel("--");
	JLabel range1 = new JLabel("--",SwingConstants.CENTER);
	JLabel value2 = new JLabel("--");
	JLabel range2 = new JLabel("--",SwingConstants.CENTER);
	JLabel value3 = new JLabel("--");
	JLabel range3 = new JLabel("--",SwingConstants.CENTER);
	JButton b0 = new JButton();
	JButton b1 = new JButton();
	JButton b2 = new JButton();
	JButton b3 = new JButton();
	MeSystem me;
	public Monitor(MeSystem me) {
		this.me=me;
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		panel.setLayout(new GridLayout(0,3));
		
		panel.add(value0);
		panel.add(b0);
		panel.add(range0);
		b0.setBackground(Color.GREEN);
		
		panel.add(value1);
		panel.add(b1);
		panel.add(range1);
		b1.setBackground(Color.GREEN);
		
		panel.add(value2);
		panel.add(b2);
		panel.add(range2);
		b2.setBackground(Color.GREEN);
		
		panel.add(value3);
		panel.add(b3);
		panel.add(range3);
		b3.setBackground(Color.GREEN);
		
		
		b0.addActionListener(event -> process(event, 0));
		b1.addActionListener(event -> process(event, 1));
		b2.addActionListener(event -> process(event, 2));
		b3.addActionListener(event -> process(event, 3));
		
		
		
		
		
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setSize(520,600);
		frame.setMinimumSize(new Dimension(520,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("value monitor");
		frame.pack();
		frame.setVisible(true);
	}
	
	
	public void updateData(double[] data) {
		value0.setText(String.valueOf(data[0]));
		value1.setText(String.valueOf(data[1]));
		value2.setText(String.valueOf(data[2]));
		value3.setText(String.valueOf(data[3]));
	}
	
	public void setRanges(String[] ranges) {
		range0.setText(ranges[0]);
		range1.setText(ranges[1]);
		range2.setText(ranges[2]);
		range3.setText(ranges[3]);
	}
	
	
	/*
	 * alarm status: 0- no alarm , 1- alarmCond , 2- alarm triggered
	 */
	public void alarmState(int id, int alarmStatus) {
		switch (id){
		 case 0:
			 if(alarmStatus==2) {
				 this.b0.setBackground(Color.RED);}
			 else if(alarmStatus==1) {
				 this.b0.setBackground(Color.ORANGE);}
			 else { this.b0.setBackground(Color.GREEN);}
			 break;
		 case 1:
			 if(alarmStatus==2) {
			 this.b1.setBackground(Color.RED);}
			 else if(alarmStatus==1) {
				 this.b1.setBackground(Color.ORANGE);}
			 else this.b1.setBackground(Color.GREEN);
			 break;
		 case 2:
			 if(alarmStatus==2) {
			 this.b2.setBackground(Color.RED);}
			 else if(alarmStatus==1) {
				 this.b2.setBackground(Color.ORANGE);}
			 else this.b2.setBackground(Color.GREEN);
			 break;
		 case 3:
			 if(alarmStatus==2) {
			 this.b3.setBackground(Color.RED);}
			 else if(alarmStatus==1) {
				 this.b3.setBackground(Color.ORANGE);}
			 else this.b3.setBackground(Color.GREEN);
			 break;
			 
		 }
		 
		
	}
	private void process(ActionEvent ae, int i) {
		this.me.ackAlarm(i);
	}
	
	public void ackLabel(boolean ack,int id) {
		String label="";
		if(ack) {label="acknowledged!";}
		switch (id){	
			case 0:
				this.b0.setText(label);
				break;
			case 1:
				this.b1.setText(label);
				break;
			case 2:
				this.b2.setText(label);
				break;
			case 3:
				this.b3.setText(label);
				break;
	
		};
	}
	
}
