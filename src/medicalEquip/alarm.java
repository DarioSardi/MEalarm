package medicalEquip;
import java.util.Date;

public class Alarm implements Runnable{

	private MeSystem mySystem;
	//SETTINGS
	private boolean isLatching;
	private int max_th;
	private int min_th;
	private int valID;
	private String priority;
	//ALARM
	private boolean alarmCondition;
	private long alarmConditionStart=0;
	private boolean visualAlarm = false;
	private boolean isActive=true;
	private boolean audioAlarm = false;
	private int alarmDelay; //based on prio or programmable?
	private long alarmRiseTime;
	//ACKNOWLEDGE
	private boolean acknowledged=false;
	private boolean isTimedAck=false;
	private long ackStartTime=0; //TODO init TBD
	private long timedAckLimit=60000; //1 min max time of ack before alarms goes off again
	//AUDIO PAUSED
	private boolean audioOff =false;
	private boolean isAudioPaused =false;
	private long audioPausedTime;	//TODO ridondante?
	
	public Alarm(boolean latch,int delay, String prio,int max,int min,int id,MeSystem s){
		this.isLatching = latch;
		this.alarmCondition=false;
		this.alarmDelay = delay;
		this.priority = prio;
		this.max_th=max;
		this.min_th=min;
		this.valID=id;
		this.mySystem = s;
		System.out.println("init alarm "+this.valID+" maxt:"+this.max_th+" minth: "+this.min_th+" delay: "+this.alarmDelay);
	}
	
	/*
	 * monitoredVal : valore della variablie controllata dall'allarme
	 * time			: tempo espresso in mill usato per gestire i timer dell'allarme
	 */
	public void run() {
		while(true) {
		if(this.isActive ) {
			long monitoredVal=mySystem.getValue(this.valID);
			//ALARM CONDITION RISING
			if ( (monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.alarmCondition && this.alarmConditionStart==0) {
				this.alarmCondition=true;
				this.alarmConditionStart=System.currentTimeMillis();
			}
			//DELAYED ALARM NOT RISING
			else if(this.alarmCondition && !(monitoredVal<this.max_th || monitoredVal>this.min_th) && !this.isLatching && !this.visualAlarm ){
				//short audio burst and log are mandatory
				this.alarmCondition=false;
				this.alarmConditionStart=0;
			}
			//DELAYED ALARM START COND
			else if(this.alarmCondition && (monitoredVal>this.max_th || monitoredVal<this.min_th) && System.currentTimeMillis()-this.alarmConditionStart>this.alarmDelay) {
				alarmOn(monitoredVal);
			}
			//ALARM AUTO RESET
			else if(this.alarmCondition && !(monitoredVal<this.max_th || monitoredVal>this.min_th) && !this.isLatching && this.visualAlarm ){
				alarmOff(monitoredVal);
			}
			
			
			//TIMED ACK RESET COND
			if(this.acknowledged && this.isTimedAck && this.ackStartTime>0  && System.currentTimeMillis()-this.ackStartTime>this.timedAckLimit) {
				this.acknowledged=false;
				this.ackStartTime=0;
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
	}
	
	private void alarmOn(long monitoredVal) {
		// this.alarmCondition=true; assert true
		this.audioAlarm=true;
		this.visualAlarm=true;
		this.alarmRiseTime= System.currentTimeMillis();
		Date date=new Date(this.alarmRiseTime);
		System.out.println(date+" alarm "+this.valID+" risen with value "+ monitoredVal);
	}
	
	private void alarmOff(long monitoredVal) {
		this.alarmCondition=false;
		this.audioAlarm=false;
		this.visualAlarm=false;
		this.alarmRiseTime= System.currentTimeMillis();
		Date date=new Date(this.alarmRiseTime);
		System.out.println(date+" alarm "+this.valID+" reset with value "+ monitoredVal);
	}
	
	/*
	 * pause audio marking time for logging and timers
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
	 * disable audio func for this alarm, can be paired with an acustic reminder signal
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
			Date date=new Date(this.audioPausedTime);
			System.out.println(date+" alarm aduio "+this.valID+" paused");
		}
	}
	
	
	
	
	/*
	 * spegne l'allarme a prescindere che sia latching o meno
	 */
	public boolean alarmReset() {
		if (this.alarmCondition) {
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
	
}
