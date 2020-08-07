package monitor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import medicalEquip.Alarm;
import medicalEquip.MeSystem;

public class Monitor {
	protected AlarmModule[] AlarmModuleList;
	protected MeSystem me;
	protected JComboBox<String> alarms;
	protected JFrame frame;
	protected JPanel myPanel;
	public Monitor() {
		System.out.println("empty monitor created for manual testing");
	}
	
	public Monitor(MeSystem me, Alarm[] alarmSet) {
		this.me=me;
		this.AlarmModuleList= new AlarmModule[alarmSet.length];
		this.frame = new JFrame();
		myPanel = new JPanel();
		myPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		myPanel.setLayout(new GridLayout(0,1));
			
		
		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			AlarmModule am = new AlarmModule();
			am.connectMonitor(this);
			am.connectAlarm(alarmSet[i]);
			am.setRanges();
			JPanel testPanel = am.getPanel();
			myPanel.add(testPanel);
			this.AlarmModuleList[i]=am;
		}
		
		this.frame.add(myPanel,BorderLayout.CENTER);
		this.frame.setSize(520,600);
		this.frame.setMinimumSize(new Dimension(520,600));
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("value monitor");
		this.frame.pack();
		this.frame.setVisible(true);
		
	}
	
	public void updateData(double[] data) {
		for (int i = 0; i < this.AlarmModuleList.length; i++) {
			this.AlarmModuleList[i].updateValue(data[i]);
		}
	}
	
}
