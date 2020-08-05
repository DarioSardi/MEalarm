package test;

import medicalEquip.Alarm;
import medicalEquip.MeSystem;
import monitor.AlarmModule;
import monitor.Monitor;

public class MonitorStub extends Monitor {
	public MonitorStub(MeSystem me, Alarm[] alarmSet) {
	super();
	for (int i = 0; i < alarmSet.length; i++) {
			AlarmModule am = new AlarmModuleStub();
			am.connectMonitor(this);
			am.connectAlarm(alarmSet[i]);
			am.setRanges();
		}
	}
	
	@Override
	public void updateData(double[] data) {
		
	}
}
