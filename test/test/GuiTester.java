package test;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import medicalEquip.Alarm;
import medicalEquip.MeSystem;
import monitor.Monitor;

public class GuiTester extends Monitor {

	public GuiTester(MeSystem mesys, Alarm[] alarmSet) {
		super(mesys,alarmSet);
		//MANUAL CONTROLS
		JPanel controls = new JPanel();
		controls.setLayout(new GridLayout(3,2));

		String[] numeroAllarmi = new String[alarmSet.length];
		for (int i = 0; i < alarmSet.length; i++) {
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
		JCheckBox random	= new JCheckBox("toggle random value gen.");
		random.addActionListener(event -> disableRandom(event));
		random.setSelected(true);
		controls.add(random);
		myPanel.add(controls);
	}

	private void disableRandom(ActionEvent e) {
		 JCheckBox cb = (JCheckBox) e.getSource();
		 if(!cb.isSelected()) {
			 this.me.autoUpdate=false;
		 }
		 else {
			 this.me.autoUpdate=true;
		 }
	}
	public static void main(String[] args) throws IOException {
		MeSystem mesys = new MeSystem();
		Monitor m= new GuiTester(mesys,mesys.alarmSet);
		mesys.attachMonitor(m);
		Thread subsystemUpdate = new Thread(mesys);
		subsystemUpdate.start();
	}
}

