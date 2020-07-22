package monitor;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import medicalEquip.Alarm;

public class AlarmModule {
	JLabel value 	= new JLabel("--",SwingConstants.CENTER);
	JLabel range 	= new JLabel("--",SwingConstants.CENTER);
	JLabel status 	= new JLabel("",SwingConstants.CENTER);
	JButton b 		= new JButton();
	JCheckBox disable	= new JCheckBox("disable alarm");
	JPanel myPanel = new JPanel();
	ImageIcon alarmOff = new ImageIcon(ImageIcon.class.getResource("/monitor/audioOff.png"));
	ImageIcon alarmOn = new ImageIcon(ImageIcon.class.getResource("/monitor/audioOn.png"));
	Alarm a;
	Monitor monitor;
	public AlarmModule() {
		this.myPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		this.myPanel.setLayout(new GridLayout(0,5));
		this.myPanel.add(this.value);
		this.b.setBackground(Color.GREEN);
		this.b.addActionListener(event -> ackClick(event));
		this.myPanel.add(this.b);
		this.myPanel.add(this.status);
		this.myPanel.add(this.range);
		this.myPanel.add(this.disable);
	}
	
	public JPanel getPanel() {
		return this.myPanel;
	}
	
	public void updateValue(double val) {
		this.value.setText(String.valueOf(val));
	}
	
	public void connectAlarm(Alarm al) {
		this.a=al;
	}
	
	public void connectMonitor(Monitor m) {
		this.monitor=m;
	}
	
	public void setRanges(String range) {
		this.range.setText("<html>"+range+"</html>");
	}
	
	public void alarmState(int alarmStatus) {
		 if(alarmStatus==2) {
			 this.b.setBackground(Color.RED);
			 this.status.setIcon(alarmOn);}
		 else if(alarmStatus==1) {
			 this.b.setBackground(Color.ORANGE);}
		 else { 
			 this.b.setBackground(Color.GREEN);
			 this.status.setIcon(null);
			 this.b.setText("");
		 }
	}
	
	public void acknowledgeUpdate(boolean ack) {
		String label="";
		ImageIcon ico = this.alarmOn;
		if(ack) {
			label="acknowledged!";
			ico	= this.alarmOff;
		}
		this.b.setText(label);
		this.status.setIcon(ico);
	}
	
	private void ackClick(ActionEvent ae) {
		this.a.acknowledged();
	}
}
