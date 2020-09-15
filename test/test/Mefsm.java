package test;

import medicalEquip.Alarm;
import medicalEquip.MeSystem;
import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

public class Mefsm extends Alarm implements FsmModel{ 
	String state;
	double myValue;
	double badValue = 5;
	double okValue = 15;
	public Mefsm() {
		super(false, 1, "LOW", 0, 30, 10 , new MeSystem());
		this.state="off";
		this.myValue=this.okValue;
	}
	
	@Override
	protected double getValue() {
		return this.myValue;
	}
	
	@Action
	public void off(){
		if(this.state=="waiting-reset") {
			this.alarmReset();
		}
		this.myValue=this.okValue;
		this.check();
		this.state="off";
	}
	
	public boolean offGuard() {
		return (this.state=="waiting-reset" || this.state=="off" || this.state=="al-condition" || this.state=="disabled" || this.state=="on" || this.state=="on-no-audio");
	}
	
	@Action
	public void alcondition(){
		this.myValue=this.badValue;
		this.check();
		this.state="al-condition";
	}
	
	public boolean alconditionGuard() {
		return (this.state=="off" || this.state=="waiting-reset");
	}
	
	@Action
	public void on(){
		this.myValue=this.badValue;
		if(this.state=="on-no-audio") {
			this.resetAck();
		}
		this.check();
		this.state="on";
	}
	
	public boolean onGuard() {
		return (this.state=="al-condition" || this.state=="on-no-audio" || this.state=="on");
	}
	
	@Action
	public void onNoAudio(){
		this.myValue=this.badValue;
		if(this.state=="on") {
			this.acknowledged();
		}
		this.check();
		this.state="on-no-audio";
	}
	
	public boolean onNoAudioGuard() {
		return (this.state=="al-condition" || this.state=="on-no-audio" || this.state=="on");
	}
	
	@Action
	public void onLatching(){
		this.state="on-latching";
	}
	
	public boolean onLatchingGuard() {
		return (this.state=="al-condition" || this.state=="on-latching-no-audio" || this.state=="on-latching");
	}
	
	@Action
	public void onLatchingNoAudio(){
		this.state="on-latching-no-audio";
	}
	
	public boolean onLatchingNoAudioGuard() {
		return (this.state=="al-condition" || this.state=="on-latching-no-audio" || this.state=="on-latching");
	}
	
	@Action
	public void waitingReset(){
		this.state="waiting-reset";
	}
	
	public boolean waitingResetGuard() {
		return (this.state=="waiting-reset" || this.state=="on-latching-no-audio" || this.state=="on-latching");
	}
	
	@Override
	public Object getState() {
		return this.state;
	}
	
	@Override
	public void reset(boolean arg0) {
		this.state="off";
		
	}
}