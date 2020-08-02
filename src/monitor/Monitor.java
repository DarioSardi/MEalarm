package monitor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
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
import javax.swing.JComboBox;

public class Monitor {
	AlarmModule[] AlarmModuleList;
	MeSystem me;
	JComboBox<String> alarms;
	JFrame frame;
	public Monitor(MeSystem me, Alarm[] alarmSet) {
		this.me=me;
		this.AlarmModuleList= new AlarmModule[alarmSet.length];
		this.frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		panel.setLayout(new GridLayout(0,1));
		
		//MANUAL CONTROLS
		JPanel controls = new JPanel();
		controls.setLayout(new GridLayout(2,2));

		String[] numeroAllarmi = new String[this.AlarmModuleList.length];
		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			numeroAllarmi[i]=String.valueOf(i);
		}
		JLabel selectList = new JLabel("select the values you want to control: ");
		controls.add(selectList);
		
		this.alarms = new JComboBox<String>(numeroAllarmi);
		controls.add(alarms);
		
		controls.add(new JScrollPane(alarms));
		JLabel l = new JLabel("use those buttons to increase or decrease the values: ");
		controls.add(l);
		
		JPanel controlsA = new JPanel();
		JButton plus = new JButton("+");
		plus.addActionListener(event -> this.me.changeValues(Integer.valueOf((String) this.alarms.getSelectedItem()), 1));
		JButton minus = new JButton("-");
		minus.addActionListener(event -> this.me.changeValues(Integer.valueOf((String) this.alarms.getSelectedItem()), -1));
		controlsA.add(plus);
		controlsA.add(minus);
		controls.add(controlsA);
		
		panel.add(controls);
		
		
		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			AlarmModule test = new AlarmModule();
			test.connectMonitor(this);
			test.connectAlarm(alarmSet[i]);
			test.setRanges();
			JPanel testPanel = test.getPanel();
			panel.add(testPanel);
			this.AlarmModuleList[i]=test;
		}
		
		this.frame.add(panel,BorderLayout.CENTER);
		this.frame.setSize(520,600);
		this.frame.setMinimumSize(new Dimension(520,600));
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("value monitor");
		this.frame.pack();
		this.frame.setVisible(true);
		
	}
	
	public Monitor(MeSystem me, Alarm[] alarmSet,boolean visible) {
		this(me, alarmSet);
		this.frame.setVisible(false);
	}
	
	
	public void updateData(double[] data) {
		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			this.AlarmModuleList[i].updateValue(data[i]);
		}
	}
	
}
