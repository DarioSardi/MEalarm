package medicalEquip;
import java.util.Date;
import monitor.AlarmModule;

public class Alarm{
	
	protected MeSystem mySystem;
	//SETTINGS
	protected /*@ spec_public non_null */boolean isLatching;
	protected/*@ non_null */double max_th;
	protected/*@ non_null */double min_th;
	public int valID;
	@SuppressWarnings("unused")
	private String priority;
	//ALARM
	protected /*@ spec_public non_null */boolean alarmCondition;
	protected long alarmConditionStart=0;
	protected /*@ spec_public @*/boolean visualAlarm = false;
	protected /*@ spec_public @*/ boolean isActive=true;
	protected /*@ spec_public @*/boolean audioAlarm = false;
	protected int alarmDelay; 
	protected long minimumAlarmTime = 3000;
	protected /*@ spec_public @*/ long alarmRiseTime; // 0 if no alarm
	//@ public invariant alarmRiseTime>=0;
	//ACKNOWLEDGE
	protected /*@ spec_public @*/boolean acknowledged=false;
	protected boolean isTimedAck=true;
	protected long ackStartTime=0; 
	protected long timedAckLimit=10000; 
	//AUDIO PAUSED
	protected /*@ spec_public @*/ boolean audioOff =false;
	protected /*@ spec_public @*/ boolean isAudioPaused =false;
	protected /*@ spec_public @*/ long audioPausedTime;
	//MONITOR
	private/*@ non_null */AlarmModule alarmMod;
	protected /*@ spec_public @*/ boolean waitingReset=false;
	
	public Alarm(boolean latch,int delay, String prio,int max,int min,int id,MeSystem s){
		this.isLatching = latch;
		this.alarmCondition=false;
		this.isActive=true;
		this.alarmDelay = delay;
		this.priority = prio;
		this.max_th=(double) max;
		this.min_th=(double) min;
		this.valID=id;
		this.mySystem = s;
	}
	protected double getValue() {
		return this.mySystem.getValue(this.valID);
	}
	/*
	 * main update function of the alarm.
	 * use this function inside the main loop of the program keeping the values updated in the MeSystem.
	 */
	/*@
	@	ensures  !(visualAlarm && waitingReset);
	@	ensures  !(audioAlarm && isAudioPaused) || (audioAlarm && audioOff); 
	*/
	public void check() {
		if(this.isActive ) {
			double monitoredVal=this.getValue();
			//ALARM CONDITION RISING
			if ( (monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.alarmCondition) {
				//assert(!this.visualAlarm ! ) : "alarm reset but audio still there";
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
				this.visualAlarm=false;
				this.audioAlarm=false;
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
	/*@
	  @ requires !visualAlarm;
	  @ requires monitoredVal<=100 && monitoredVal>=0;
	  @ ensures visualAlarm;
	  @*/
	protected void alarmOn(double monitoredVal) {
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
	/*@
	  @ requires visualAlarm;
	  @ ensures !visualAlarm;
	  @*/
	protected void alarmOff(double monitoredVal) {
		assert this.alarmCondition==true : "alarm turning off without alarm condition";
		this.alarmCondition=false;
		this.alarmConditionStart=0;
		resetAck();
		this.audioAlarm=false;
		if(!this.waitingReset) {assert this.visualAlarm==true : "alarm on without visual signal!";}
		this.visualAlarm=false;
		updateMonitor(0);
		this.alarmRiseTime= 0;
		Date date=new Date(System.currentTimeMillis());
		System.out.println(date+" alarm "+this.valID+" reset with value "+ monitoredVal);
	}
	

	/*
	 * disable audio func for this alarm, should be paired with an acustic reminder signal.
	 */
	/*@
	  @ ensures audioOff && !audioAlarm;
	  @*/
	public void disableAudio() {
		this.audioOff=true;
		this.audioAlarm=false;
	}
	
	/*
	 * re-enable audio after a manual exclusion
	 */
	/*@
	  @ requires audioOff;
	  @ ensures !audioOff;
	  @*/
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
	/*@
	  @ensures (audioAlarm && alarmCondition)==>(isAudioPaused && !audioAlarm && acknowledged); 
	  @*/
	public void acknowledged() {
		if(this.audioAlarm && this.alarmCondition) {
			this.audioAlarm=false;	
			this.isAudioPaused=true;
			this.audioPausedTime= System.currentTimeMillis();
			this.ackStartTime=System.currentTimeMillis();
			this.acknowledged=true;
			Date date=new Date(this.audioPausedTime);
			System.out.println(date+" alarm "+this.valID+" acknowledged");
			this.ackUpdate(true);
		}
	}
	
	/*
	 * private function used to reset ack in case of timer expiration or alarm switching off while acknowledged
	 */
	/*@
	  @ requires acknowledged;
	  @ ensures !acknowledged;
	  @ ensures (isAudioPaused && audioPausedTime>0 && audioOff) ==> (audioAlarm && !isAudioPaused) ;
	  @*/
	protected void resetAck(){
		this.acknowledged=false;
		this.ackStartTime=0;
		this.ackUpdate(false);
		//audio paused or off?
		if( this.isAudioPaused && this.audioPausedTime>0  && !this.audioOff) {
			this.audioAlarm= true;
			this.audioPausedTime=0;
			this.isAudioPaused =false;
		}
	}
	
	protected void ackUpdate(Boolean status) {
		this.alarmMod.acknowledgeUpdate(status);
	}
	
	
	/*
	 * Turn off a latching alarm
	 */
	/*@
	  @ requires isLatching;
	  @ ensures waitingReset ==> !waitingReset;
	  @*/
	public boolean alarmReset() {
		if (this.isLatching && this.waitingReset) {
			this.alarmOff(this.mySystem.getValue(this.valID));
			this.waitingReset=false;
			return true;
		}
		else return false;
		
	}
	
	/*
	 * deactivate alarm
	 * @param status: if false alarm is deactivated, if true alarm is re-enabled
	 */
	/*@
	  @  ensures isActive <==> status;
	  @*/
	public void alarmInactivationStateSwitch(boolean status) {
		if(this.isActive && !status) {
			this.isActive=false;
		}
		else if(!this.isActive && status){
			this.isActive=true;
			}
	}
	
	/*
	 * monitor methods
	 */
	public void attachMonitor(AlarmModule m) {
		this.alarmMod = m;
	}
	
	protected void updateMonitor(int a) {
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
	
	public boolean isVisualAlarm() {
		return this.visualAlarm;
	}	
	
	public boolean isAudioAlarm() {
		return this.audioAlarm;
	}
	
	
	public boolean isWaitingReset() {
		return this.waitingReset;
	}

	
	
	
}
