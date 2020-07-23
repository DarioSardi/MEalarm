package monitor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.sun.prism.Image;

import medicalEquip.Alarm;

public class AlarmModule {
	JLabel value 	= new JLabel("--",SwingConstants.CENTER);
	JLabel range 	= new JLabel("--",SwingConstants.CENTER);
	JLabel status 	= new JLabel("",SwingConstants.CENTER);
	JButton b 		= new JButton();
	JCheckBox disable	= new JCheckBox("disable alarm");
	JButton reset	= new JButton("reset latch alarm");
	JPanel extraPanel = new JPanel();
	JPanel myPanel = new JPanel();
	ImageIcon alarmOff = new ImageIcon(ImageIcon.class.getResource("/monitor/audioOff.png"));
	ImageIcon alarmOn = new ImageIcon(ImageIcon.class.getResource("/monitor/audioOn.png"));
	ImageIcon disabled = new ImageIcon(ImageIcon.class.getResource("/monitor/disabled.png"));
	
	Alarm a;
	Monitor monitor;
	private boolean isDeactivated = false;
	private int lastScale=0;
	public AlarmModule() {
		this.myPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		this.myPanel.setLayout(new GridLayout(0,5));
		this.myPanel.add(this.value);
		this.b.setBackground(Color.GREEN);
		this.b.addActionListener(event -> ackClick(event));
		this.myPanel.add(this.b);
		this.myPanel.add(this.status);
		this.myPanel.add(this.range);
		//this.myPanel.add(this.disable);
	}
	
	public JPanel getPanel() {
		return this.myPanel;
	}
	
	public void updateValue(double val) {
		this.value.setText("value "+this.a.valID+" : "+ String.valueOf((int)val));
	}
	
	public void connectAlarm(Alarm al) {
		this.a=al;
		this.a.attachMonitor(this);
		this.disable.addActionListener(event -> disableCheck(event));
		if(this.a.isLatching()) {
			this.extraPanel.setLayout(new GridLayout(2,0));
			this.extraPanel.add(this.disable);
			this.reset.addActionListener(event -> this.a.alarmReset());
			this.extraPanel.add(this.reset);
			this.myPanel.add(extraPanel);
		}
		else {
			this.myPanel.add(this.disable);
		}
	}
		
	private void disableCheck(ActionEvent e) {
		 JCheckBox cb = (JCheckBox) e.getSource();
		 this.a.alarmInactivationStateSwitch(!cb.isSelected());
		 this.isDeactivated = cb.isSelected();
		 if(this.isDeactivated){
			 this.b.setBackground(Color.GRAY);
			 this.updateIcons();
			 this.status.setIcon(disabled);}
		 else {
			 this.alarmState(0);
		 }
		 this.a.check();
		}
		
	
	public void connectMonitor(Monitor m) {
		this.monitor=m;
	}
	
	public void setRanges() {
		String s=this.a.getRanges();
		this.range.setText("<html>"+s+"</html>");
	}
	
	private void updateIcons() {
		if(this.lastScale!=this.status.getHeight()) {
			this.alarmOff =this.getScaledImage("/monitor/audioOff.png");
			this.alarmOn=this.getScaledImage("/monitor/audioOn.png");	
			this.disabled=this.getScaledImage("/monitor/disabled.png");	
			
		}		
	}
	
	public void alarmState(int alarmStatus) {
		this.updateIcons();
		if(!this.isDeactivated) {
		if(alarmStatus==3) {
			 this.b.setBackground(Color.BLUE);
			 this.status.setIcon(alarmOn);}
		else if(alarmStatus==2) {
			 this.b.setBackground(Color.RED);
			 this.status.setIcon(alarmOn);}
		 else if(alarmStatus==1) {
			 this.b.setBackground(Color.ORANGE);}
		 else { 
			 this.b.setBackground(Color.GREEN);
			 this.status.setIcon(null);
			 this.b.setText("");
		 }}
	}
	
	public void acknowledgeUpdate(boolean ack) {
		String label="";
		ImageIcon ico = this.alarmOn;
		if(ack) {
			label="acknowledged!";
			ico	= this.alarmOff;
		}
		this.b.setText(label);
		this.updateIcons();
		this.status.setIcon(ico);
	}
	
	private void ackClick(ActionEvent ae) {
		this.a.acknowledged();
	}
	
	private ImageIcon getScaledImage(String src){
		ImageIcon ico= new ImageIcon(ImageIcon.class.getResource(src));
		java.awt.Image image = ico.getImage();
		int w= this.status.getHeight();
		int h= w;
		this.lastScale=w;
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(image, 0, 0, w, h, null);
	    g2.dispose();
	    return new ImageIcon(resizedImg);
	}
}
