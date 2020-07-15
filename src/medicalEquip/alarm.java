package medicalEquip;
import java.util.Date;

public class alarm {
	private boolean isActive;
	private boolean isLatching;
	private int max_th;
	private int min_th;
	private int valID;
	private String priority;
	private boolean alarmCondition;
	private boolean visualAlarm = false;
	private boolean audioAlarm = false;
	
	private int alarmLimit;
	private long alarmRiseTime;
	
	public alarm(boolean latch,int limit, String prio,int max,int min,int id){
		this.isLatching = latch;
		this.alarmCondition=false;
		this.alarmLimit = limit;
		this.priority = prio;
		this.max_th=max;
		this.min_th=min;
		this.valID=id;
	}
	
	/*
	 * monitoredVal : valore della variablie controllata dall'allarme
	 * time			: tempo espresso in mill usato per gestire i timer dell'allarme
	 */
	public void update(meSystem sys) {
		long monitoredVal=sys.getValue(this.valID);
		if (this.isActive && (monitoredVal>this.max_th || monitoredVal<this.min_th) ) {
			alarmOn();
		}
		else if(this.alarmCondition && !this.isLatching &&  System.currentTimeMillis()-this.alarmRiseTime>alarmLimit ){
			alarmOff();
		}
	}
	
	private void alarmOn() {
		this.alarmCondition=true;
		this.audioAlarm=true;
		this.visualAlarm=true;
		this.alarmRiseTime= System.currentTimeMillis();
		Date date=new Date(this.alarmRiseTime);
		System.out.println(date+" alarm "+this.valID+" risen");
	}
	
	private void alarmOff() {
		this.alarmCondition=false;
		this.audioAlarm=false;
		this.visualAlarm=false;
		this.alarmRiseTime= System.currentTimeMillis();
		Date date=new Date(this.alarmRiseTime);
		System.out.println(date+" alarm "+this.valID+" reset");
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
	public boolean alarmInactivationStateSwitch() {
		if(this.isActive) {
			this.isActive=false;
			return false;
		}
		else {
			this.isActive=true;
			return true;}
	}
	
}
