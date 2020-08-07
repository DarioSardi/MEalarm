package test;

import java.util.concurrent.ThreadLocalRandom;

import medicalEquip.Alarm;
import medicalEquip.MeSystem;
import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

public class AlarmStubModel extends Alarm implements FsmModel{
	double myValue;
	public AlarmStubModel(boolean latch, int delay, String prio, int max, int min, int id, MeSystem s) {
		super(latch, delay, prio, max, min, id, s);
	}
	
	@Override
	protected double getValue() {
		return this.myValue;
	}
	
	@Override
	public Object getState() {
		if(!this.alarmCondition && !this.visualAlarm) {return "OFF";}
		if(this.alarmCondition && !this.visualAlarm) {return "ALARMCONDITION";}
		if(this.visualAlarm && !this.isLatching && this.audioAlarm) {return "ON AUDIOON";}
		if(this.visualAlarm && !this.isLatching && !this.audioAlarm) {return "ON AUDIOFF";}
		if(this.visualAlarm && this.isLatching && this.audioAlarm) {return "ON_LATCHING AUDIOON";}
		if(this.visualAlarm && this.isLatching && !this.audioAlarm) {return "ON_LATCHING AUDIOOFF";}
		if(this.waitingReset && !this.visualAlarm && this.isLatching) {return "WAITING_RESET";}
		if(!this.isActive) {return "DISABLED";}
		return false;
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
	}
	
	@Action
	public void allarmingValue() {
		this.myValue = ThreadLocalRandom.current().nextDouble(this.max_th,100);
		this.check();
	}
	
	@Action
	public void okValue() {
		this.myValue = ThreadLocalRandom.current().nextDouble(this.min_th,this.max_th);
		this.check();
	}
	
	@Action
	public void ackThat() {
		this.acknowledged();
	}
	
	@Action
	public void disable() {
		this.alarmInactivationStateSwitch(false);
	}
	@Action
	public void enable() {
		this.alarmInactivationStateSwitch(true);
	}
}
