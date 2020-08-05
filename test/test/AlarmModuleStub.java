package test;

import monitor.AlarmModule;

public class AlarmModuleStub extends AlarmModule{
	public AlarmModuleStub() {
	}
	
	@Override
	public void alarmState(int alarmStatus) {
		//nothing to do here
	}
	
	@Override
	public void acknowledgeUpdate(boolean ack) {
		//still nothing
	}
}
