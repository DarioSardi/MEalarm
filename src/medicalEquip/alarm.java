package medicalEquip;
import java.util.Date;

public class Alarm implements Runnable{
	private boolean isActive=true;
	private boolean isLatching;
	private int max_th;
	private int min_th;
	private int valID;
	private String priority;
	private boolean alarmCondition;
	private boolean visualAlarm = false;
	private boolean audioAlarm = false;
	private MeSystem mySystem;
	
	private int alarmLimit;
	private long alarmRiseTime;
	
	public Alarm(boolean latch,int limit, String prio,int max,int min,int id,MeSystem s){
		this.isLatching = latch;
		this.alarmCondition=false;
		this.alarmLimit = limit;
		this.priority = prio;
		this.max_th=max;
		this.min_th=min;
		this.valID=id;
		this.mySystem = s;
		System.out.println("init alarm "+this.valID+" maxt:"+this.max_th+" minth: "+this.min_th+" limit: "+this.alarmLimit);
	}
	
	/*
	 * monitoredVal : valore della variablie controllata dall'allarme
	 * time			: tempo espresso in mill usato per gestire i timer dell'allarme
	 */
	public void run() {
		while(true) {
		long monitoredVal=mySystem.getValue(this.valID);
		//System.out.println(monitoredVal+" maxt:"+this.max_th+" minth: "+this.min_th);
		if (this.isActive && (monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.alarmCondition ) {
			alarmOn(monitoredVal);
		}
		else if(this.alarmCondition && !(monitoredVal>this.max_th || monitoredVal<this.min_th) && !this.isLatching &&  System.currentTimeMillis()-this.alarmRiseTime>alarmLimit ){
			alarmOff(monitoredVal);
		}
		}
	}
	
	private void alarmOn(long monitoredVal) {
		this.alarmCondition=true;
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
