package test;

import medicalEquip.Alarm;
import medicalEquip.MeSystem;
import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

public class AlarmStubModel extends Alarm implements FsmModel{
	double myValue;
	String status;
	public AlarmStubModel(boolean latch, int delay, String prio, int max, int min, int id, MeSystem s) {
		super(latch, delay, prio, max, min, id, s);
	}
	
	public AlarmStubModel(){
		super(false, 1, "LOW", 0, 30, 10 , new MeSystem());
		this.status = "OFF";
		this.isActive= true;
		this.minimumAlarmTime=1;
		//AlarmModule am = new AlarmModule();
		//this.attachMonitor(am);
	}

	@Override
	protected void ackUpdate(Boolean status) {
		//ok
	}
	
	
	@Override
	public void check() {
		double monitoredVal = this.getValue();
		if(!this.isActive) {
			if ( (monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.alarmCondition) {
				this.alarmCondition=true;
				this.alarmConditionStart=System.currentTimeMillis();
		}
		if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.visualAlarm ){
			this.alarmCondition=false;
			this.alarmConditionStart=0;
		}
		if(this.alarmCondition && (!this.visualAlarm || (this.visualAlarm && this.waitingReset) )&&(monitoredVal>this.max_th || monitoredVal<this.min_th) /*&& System.currentTimeMillis()-this.alarmConditionStart>this.alarmDelay*/) {
			if(this.waitingReset) {
				this.waitingReset=false;
			}
			alarmOn(monitoredVal);
		}
		if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.isLatching && this.visualAlarm && this.alarmRiseTime>0 /*&& System.currentTimeMillis()-this.alarmRiseTime>this.minimumAlarmTime*/){
			alarmOff(monitoredVal);
		}
		if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && this.isLatching && this.visualAlarm && this.alarmRiseTime>0 /*&& System.currentTimeMillis()-this.alarmRiseTime>this.minimumAlarmTime*/){
			this.waitingReset=true;
			this.visualAlarm=false;
			this.audioAlarm=false;
		}
		if(this.acknowledged && this.isTimedAck && this.ackStartTime>0  /*&& System.currentTimeMillis()-this.ackStartTime>this.timedAckLimit*/) {
			resetAck();
			
		}
		
		}
	}
	
	@Override
	protected void alarmOn(double monitoredVal) {
		if(!this.audioOff && !this.isAudioPaused) {
			this.audioAlarm=true;
		}
		this.visualAlarm=true;
		this.alarmRiseTime= System.currentTimeMillis();
	}
	
	@Override
	protected double getValue() {
		return this.myValue;
	}
	
	@Override
	public Object getState() {
		return this.status;
		}

	@Override
	public void reset(boolean arg0) {
		this.alarmCondition=false;
		this.visualAlarm=false;
		this.audioAlarm=false;
		this.acknowledged=false;
		this.waitingReset=false;
		this.ackStartTime=0;
		this.alarmConditionStart=0;
		this.audioOff=false;
		this.audioPausedTime=0;
		this.isAudioPaused=false;
		this.status="OFF";
		this.isActive= true;
	}
	
	@Override
	protected void updateMonitor(int a) {
		//ok
	}
	
	protected void alarmOff(double monitoredVal) {
		this.alarmCondition=false;
		this.alarmConditionStart=0;
		resetAck();
		this.audioAlarm=false;
		this.visualAlarm=false;
		this.alarmRiseTime= 0;
	}
	
	@Action
	public void allarmingValue() {
		this.myValue = 99;
		this.check();
		this.updateStatus();
	}
	
	@Action
	public void okValue() {
		this.myValue = 5;
		this.check();
		this.updateStatus();
	}
	
	@Action
	public void ackThat() {
		this.acknowledged();
		this.updateStatus();
		
	}
	public boolean ackThatGuard() {
		if(this.isVisualAlarm()) {return true;}
		else {return false;}
	}
	
	@Action
	public void disable() {
		this.alarmInactivationStateSwitch(false);
		this.status="DISABLED";
	}
	
	public boolean disableGuard() {
		return this.isActive;
	}
	
	
	
	@Action
	public void enable() {
		this.alarmInactivationStateSwitch(true);
		this.check();
		this.updateStatus();
	}
	
	public boolean enableGuard() {
		return !this.isActive;
	}
	
	
	public void updateStatus() {
		if(this.alarmCondition && !this.visualAlarm && !this.acknowledged) {this.status="ALARM CONDITION";}
		else if(this.visualAlarm && !this.acknowledged) {
			this.status="ON AUDIO ON:"+this.audioAlarm+"rise:"+this.alarmRiseTime;		
		}
		else if(this.waitingReset) {this.status="WAITING_RESET";}
		else if(!this.alarmCondition && !this.visualAlarm){this.status="OFF";}
		else {
			this.status="ERROR AC:"+this.alarmCondition+" VIS:"+this.visualAlarm;	
			}
		}
}
