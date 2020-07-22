package medicalEquip;
import java.util.Date;

import monitor.Monitor;

public class Alarm{

	private MeSystem mySystem;
	private Monitor monitor;
	//SETTINGS
	private boolean isLatching;
	private double max_th;
	private double min_th;
	private int valID;
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
	 * monitoredVal : valore della variablie controllata dall'allarme
	 * time			: tempo espresso in mill usato per gestire i timer dell'allarme
	 */
	public void check() {
		if(this.isActive ) {
			double monitoredVal=mySystem.getValue(this.valID);
			//ALARM CONDITION RISING
			if ( (monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.alarmCondition) {
				this.alarmCondition=true;
				updateMonitor(1);
				this.alarmConditionStart=System.currentTimeMillis();
			}
			//DELAYED ALARM NOT RISING
			if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.isLatching && !this.visualAlarm ){
				//short audio burst and log are mandatory
				this.alarmCondition=false;
				updateMonitor(0);
				this.alarmConditionStart=0;
			}
			//DELAYED ALARM START COND
			if(this.alarmCondition && !this.visualAlarm &&(monitoredVal>this.max_th || monitoredVal<this.min_th) && System.currentTimeMillis()-this.alarmConditionStart>this.alarmDelay) {
				alarmOn(monitoredVal);
			}
			//ALARM AUTO RESET
			if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.isLatching && this.visualAlarm && this.alarmRiseTime>0 && System.currentTimeMillis()-this.alarmRiseTime>this.minimumAlarmTime){
				alarmOff(monitoredVal);
			}
			
			
			//TIMED ACK RESET COND
			if(this.acknowledged && this.isTimedAck && this.ackStartTime>0  && System.currentTimeMillis()-this.ackStartTime>this.timedAckLimit) {
				Date date=new Date(System.currentTimeMillis());
				System.out.println(date+" acknowledge for "+this.valID+" expired");
				this.acknowledged=false;
				this.ackStartTime=0;
				this.monitor.ackLabel(false, this.valID);
				//audio paused or off?
				if( this.isAudioPaused && this.audioPausedTime>0  && !this.audioOff) {
					this.audioAlarm= true;
					this.audioPausedTime=0;
					this.isAudioPaused =false;
				}
				
			}
			
			//TODO alarm sonozeeeeeeeer timer check
			}
		}
	
	/**
	 * Turn the alarm on.
	 * Turn the sound on, the video alarm on, update the gui, save the trigger time and log the event
	 * @param monitoredVal value that triggered the alarm for log message
	 */
	private void alarmOn(double monitoredVal) {
		// this.alarmCondition=true; assert true
		this.audioAlarm=true;
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
		this.alarmCondition=false;
		this.alarmConditionStart=0;
		this.audioAlarm=false;
		this.visualAlarm=false;
		//TODO ack reset
		updateMonitor(0);
		this.alarmRiseTime= 0;
		Date date=new Date(System.currentTimeMillis());
		System.out.println(date+" alarm "+this.valID+" reset with value "+ monitoredVal);
	}
	
	/*
	 * Pause the audio alarm.
	 * Pause the audio saving the time of this action for logging and timers.
	 */
	public void pauseAudio() {
		if(this.audioAlarm && this.alarmCondition) {
			this.audioAlarm=false;	
			this.isAudioPaused=true;
			this.audioPausedTime= System.currentTimeMillis();
			Date date=new Date(this.audioPausedTime);
			System.out.println(date+" alarm aduio "+this.valID+" paused");
		}
	}
	
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
			this.monitor.ackLabel(true, this.valID);
		}
	}
	
	
	
	
	/*
	 * Reset a latching alarm
	 */
	public boolean alarmReset() {
		if (this.isLatching) {
			this.alarmCondition=false;
			return true;
		}
		else return false;
		
	}
	
	/*
	 * cicla fra allarme attivo e disattivo per escluderlo o riattivarlo
	 */
	public boolean alarmInactivationStateSwitch(boolean audio, boolean alarmSignal) {
		if(this.isActive) {
			this.isActive=false;
			return false;
		}
		else {
			this.isActive=true;
			return true;}
	}
	
	public void attachMonitor(Monitor m) {
		this.monitor=m;
	}
	
	private void updateMonitor(int a) {
		this.monitor.alarmState(this.valID, a);}
	
	
	
}
