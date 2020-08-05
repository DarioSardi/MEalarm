package medicalEquip;
import java.util.Date;


import monitor.AlarmModule;
import monitor.Monitor;

public class Alarm{

	private MeSystem mySystem;
	private Monitor monitor;
	//SETTINGS
	private boolean isLatching;
	private double max_th;
	private double min_th;
	public int valID;
	private String priority;
	//ALARM
	private boolean alarmCondition;
	private long alarmConditionStart=0;
	private boolean visualAlarm = false;
	private boolean isActive=true;
	private boolean audioAlarm = false;
	private int alarmDelay; //based on prio or programmable?
	private long minimumAlarmTime = 3000;
	private long alarmRiseTime; // 0 if no alarm
	//ACKNOWLEDGE
	private boolean acknowledged=false;
	private boolean isTimedAck=true;
	private long ackStartTime=0; //TODO init TBD
	private long timedAckLimit=10000; //1 min max time of ack before alarms goes off again
	//AUDIO PAUSED
	private boolean audioOff =false;
	private boolean isAudioPaused =false;
	private long audioPausedTime;	//TODO ridondante?
	//MONITOR
	private AlarmModule alarmMod;
	private boolean waitingReset=false;
	
	public Alarm(boolean latch,int delay, String prio,int max,int min,int id,MeSystem s){
		this.isLatching = latch;
		this.alarmCondition=false;
		this.alarmDelay = delay;
		this.priority = prio;
		this.max_th=(double) max;
		this.min_th=(double) min;
		this.valID=id;
		this.mySystem = s;
		//System.out.println("init alarm "+this.valID+" maxt:"+this.max_th+" minth: "+this.min_th+" delay: "+this.alarmDelay);
	}
	
	/*
	 * main update function of the alarm.
	 * use this function inside the main loop of the program keeping the values updated in the MeSystem.
	 */
	public void check() {
		if(this.isActive ) {
			double monitoredVal=this.mySystem.getValue(this.valID);
			//ALARM CONDITION RISING
			if ( (monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.alarmCondition) {
				this.alarmCondition=true;
				if(!this.waitingReset) {
					updateMonitor(1);
				}
				this.alarmConditionStart=System.currentTimeMillis();
			}
			//DELAYED ALARM NOT RISING
			if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.visualAlarm ){
				//short audio burst and log are mandatory
				this.alarmCondition=false;
				if(!this.waitingReset) {
				updateMonitor(0);}
				this.alarmConditionStart=0;
			}
			//DELAYED ALARM START COND
			if(this.alarmCondition && (!this.visualAlarm || (this.visualAlarm && this.waitingReset) )&&(monitoredVal>this.max_th || monitoredVal<this.min_th) && System.currentTimeMillis()-this.alarmConditionStart>this.alarmDelay) {
				if(this.waitingReset) {
					this.waitingReset=false;
					Date date=new Date(this.alarmRiseTime);
					System.out.println(date+" alarm "+this.valID+" risen again with value "+ monitoredVal+" wile waiting for reset.");
				}
				alarmOn(monitoredVal);
			}
			//ALARM AUTO RESET
			if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.isLatching && this.visualAlarm && this.alarmRiseTime>0 && System.currentTimeMillis()-this.alarmRiseTime>this.minimumAlarmTime){
				alarmOff(monitoredVal);
				assert this.alarmCondition==false : "alarm reset but alarmCond still there";
				assert this.acknowledged==false : "alarm reset but still acknowledged";
				assert this.audioAlarm==false : "alarm reset but audio still there";
				assert this.visualAlarm==false : "alarm reset but video still there";
			}
			//LATCHING ALARM CONDITION CLEAR
			if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && this.isLatching && this.visualAlarm && this.alarmRiseTime>0 && System.currentTimeMillis()-this.alarmRiseTime>this.minimumAlarmTime){
				this.updateMonitor(3);
				if(!this.waitingReset) {
				Date date=new Date(System.currentTimeMillis());
				System.out.println(date+" alarm "+this.valID+" condition clear. Alarm is latching manual reset needed.");
				}
				this.waitingReset=true;
			}
			
			
			//TIMED ACK RESET COND
			if(this.acknowledged && this.isTimedAck && this.ackStartTime>0  && System.currentTimeMillis()-this.ackStartTime>this.timedAckLimit) {
				Date date=new Date(System.currentTimeMillis());
				System.out.println(date+" acknowledge for "+this.valID+" expired");
				resetAck();
				
			}
			}
		}
	
	/**
	 * Turn the alarm on.
	 * Turn the sound on, the video alarm on, update the gui, save the trigger time and log the event
	 * @param monitoredVal value that triggered the alarm for log message
	 */
	private void alarmOn(double monitoredVal) {
		assert this.alarmCondition=true : "alarm turning on without alarm condition";
		if(!this.audioOff && !this.isAudioPaused) {
			this.audioAlarm=true;
		}
		this.visualAlarm=true;
		updateMonitor(2);
		this.alarmRiseTime= System.currentTimeMillis();
		Date date=new Date(this.alarmRiseTime);
		System.out.println(date+" alarm "+this.valID+" risen with value "+ monitoredVal);
	}
	
	/**
	 * Turn the alarm off.
	 * Turn the visual and audio alarm off, update the gui, reset trigger time and log the event
	 * @param monitoredVal last value that excluded the alarm for log message
	 */
	private void alarmOff(double monitoredVal) {
		assert this.alarmCondition==true : "alarm turning off without alarm condition";
		this.alarmCondition=false;
		this.alarmConditionStart=0;
		resetAck();
		this.audioAlarm=false;
		assert this.visualAlarm==true : "alarm on without visual signal!";
		this.visualAlarm=false;
		updateMonitor(0);
		this.alarmRiseTime= 0;
		Date date=new Date(System.currentTimeMillis());
		System.out.println(date+" alarm "+this.valID+" reset with value "+ monitoredVal);
	}
	
	/*
	 * Pause the audio alarm.
	 * Pause the audio saving the time of this action for logging and timers.
	public void pauseAudio() {
		if(this.audioAlarm && this.alarmCondition) {
			this.audioAlarm=false;	
			this.isAudioPaused=true;
			this.audioPausedTime= System.currentTimeMillis();
			Date date=new Date(this.audioPausedTime);
			System.out.println(date+" alarm aduio "+this.valID+" paused");
		}
	}*/
	
	/*
	 * disable audio func for this alarm, should be paired with an acustic reminder signal.
	 */
	public void disableAudio() {
		this.audioOff=true;
		this.audioAlarm=false;
	}
	
	/*
	 * re-enable audio after a manual exclusion
	 */
	public void enableAudio() {
		this.audioOff=false;
		if(this.visualAlarm) {
			this.audioAlarm=true;
		}
	}
	
	
	/*
	 * rise an ack signal that stops audio notification.
	 * marks time for logging and timers.
	 */
	public void acknowledged() {
		if(this.audioAlarm && this.alarmCondition) {
			this.audioAlarm=false;	
			this.isAudioPaused=true;
			this.audioPausedTime= System.currentTimeMillis();
			this.ackStartTime=System.currentTimeMillis();
			this.acknowledged=true;
			Date date=new Date(this.audioPausedTime);
			System.out.println(date+" alarm "+this.valID+" acknowledged");
			this.alarmMod.acknowledgeUpdate(true);
		}
	}
	
	private void resetAck(){
		this.acknowledged=false;
		this.ackStartTime=0;
		this.alarmMod.acknowledgeUpdate(false);
		//audio paused or off?
		if( this.isAudioPaused && this.audioPausedTime>0  && !this.audioOff) {
			this.audioAlarm= true;
			this.audioPausedTime=0;
			this.isAudioPaused =false;
		}
	}
	
	
	
	
	/*
	 * Reset a latching alarm
	 */
	public boolean alarmReset() {
		if (this.isLatching) {
			this.alarmOff(this.mySystem.getValue(this.valID));
			this.waitingReset=false;
			return true;
		}
		else return false;
		
	}
	
	/*
	 * cicla fra allarme attivo e disattivo per escluderlo o riattivarlo
	 */
	public void alarmInactivationStateSwitch(boolean status) {
		if(this.isActive && !status) {
			this.isActive=false;
		}
		else if(!this.isActive && status){
			this.isActive=true;
			}
	}
	
	public void attachMonitor(AlarmModule m) {
		this.alarmMod = m;
	}
	
	private void updateMonitor(int a) {
		this.alarmMod.alarmState(a);
	
	}
	
	public String getRanges() {
		String s="max: "+String.valueOf(this.max_th)+"<br/> min: "+String.valueOf(this.min_th);
		return s;
	}
	
	public boolean isLatching() {
		return this.isLatching;
	}
	
	/*
	 * getter for testing
	 */
	public boolean isAlarmCondition() {
		return this.alarmCondition;
	}
	/*
	 * getter for testing
	 */
	public boolean isVisualAlarm() {
		return this.visualAlarm;
	}	
	/*
	 * getter for testing
	 */
	public boolean isAudioAlarm() {
		return this.audioAlarm;
	}
	
	/*
	 * getter for testing
	 */
	public boolean isWaitingReset() {
		return this.waitingReset;
	}
	
	
}
