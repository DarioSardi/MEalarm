package monitor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import medicalEquip.Alarm;
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
import javax.swing.JCheckBox;

public class Monitor {
	AlarmModule[] AlarmModuleList;
	public Monitor(MeSystem me, Alarm[] alarmSet) {
		this.me=me;
		this.AlarmModuleList= new AlarmModule[alarmSet.length];
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		panel.setLayout(new GridLayout(0,1));

		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			AlarmModule test = new AlarmModule();
			test.connectMonitor(this);
			test.connectAlarm(alarmSet[i]);
			JPanel testPanel = test.getPanel();
			panel.add(testPanel);
			this.AlarmModuleList[i]=test;
		}
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setSize(520,600);
		frame.setMinimumSize(new Dimension(520,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("value monitor");
		frame.pack();
		frame.setVisible(true);
	}
	
	
	public void updateData(double[] data) {
		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			this.AlarmModuleList[i].updateValue(data[i]);
		}
	}
	
	
	
	//TODO Una volta connesso gli allarmi al monitor da rendere interna
	
	public void setRanges(String[] ranges) {
		System.out.println(ranges.length+" "+this.AlarmModuleList.length);
		for (int i = 0; i < this.AlarmModuleList.length; i=i+1) {
			System.out.println(ranges[i]);
			this.AlarmModuleList[i].setRanges(ranges[i]);
		}
	}
	
	
	/*
	 * alarm status: 0- no alarm , 1- alarmCond , 2- alarm triggered
	 */
	public void alarmState(int id, int alarmStatus) {
		this.AlarmModuleList[id].alarmState(alarmStatus);		
	}
	
	public void ackLabel(boolean ack,int id) {
		this.AlarmModuleList[id].acknowledgeUpdate(ack);
	}
	
}
