package medicalEquip;
import java.util.concurrent.ThreadLocalRandom;
public class meSystem implements Runnable{
	int ALARMSNUMBER =4;
	private boolean machineOn;
	private long[] data = new long[ALARMSNUMBER];
	public meSystem() {
		this.machineOn=true;
	}
	
	public long getValue(int ID) {
		return data[ID];
	}
	
	private void updateValues() {
		data[0]= ThreadLocalRandom.current().nextLong(0,100);
		data[1]= ThreadLocalRandom.current().nextLong(0,100);
		data[2]= ThreadLocalRandom.current().nextLong(0,100);
		data[3]= ThreadLocalRandom.current().nextLong(0,100);
	}
	
	 public void run() {
		 while(this.machineOn) {
		 updateValues();
		 }
	 }
	
	public static void main(String[] args) {
		meSystem mesys = new meSystem();
		Thread subsystemUpdate = new Thread(mesys);
		subsystemUpdate.start();
	
	}

}
