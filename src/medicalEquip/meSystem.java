package medicalEquip;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

public class MeSystem implements Runnable{
	int ALARMSNUMBER =4;
	private boolean machineOn;
	private float[] data = new float[ALARMSNUMBER];
	private Alarm[] alarmSet = new Alarm[ALARMSNUMBER];
	private boolean slowRun = false;
	private Monitor myMonitor=null;
	public MeSystem() throws IOException {
		this.machineOn=true;
		String row;
		URL url = getClass().getResource("alarmsData.txt");
		BufferedReader csvReader = new BufferedReader(new FileReader(url.getPath()));
		for (int i = 0; i < alarmSet.length; i++) {
			row = csvReader.readLine();
			String[] data =row.split(",");
			this.alarmSet[i]= new Alarm(Boolean.parseBoolean(data[0]),Integer.parseInt(data[1])*1000,"LOW", Integer.parseInt(data[2]),Integer.parseInt(data[3]), i,this);
		}
		this.data[0]=ThreadLocalRandom.current().nextLong(5,20);
		this.data[1]=ThreadLocalRandom.current().nextLong(10,40);
		this.data[2]=ThreadLocalRandom.current().nextLong(90,100);
		this.data[3]=ThreadLocalRandom.current().nextLong(0,20);
		
		csvReader.close();
		for(Alarm a: this.alarmSet) {
			Thread t = new Thread(a);
			t.start();
		}
		
		System.out.println("init done");
	}
	
	public long getValue(int ID) {
		return (long) this.data[ID];
	}
	
	public void attachMonitor(Monitor m) {
		this.myMonitor=m;
	}
	
	
	/*
	 * necessari valori con rumore simil-perlin per garantire possibili allarmi prolungati
	 */
	private void updateValues() {
		this.data[0] = (float) (this.data[0] + ThreadLocalRandom.current().nextFloat()*2-1);
		if (this.data[0]<0) this.data[0]=0;	if (this.data[0]>100) this.data[0]=100;
		data[1]= (float) (this.data[1] + ThreadLocalRandom.current().nextFloat()*2-1);
		if (this.data[1]<0) this.data[1]=0;	if (this.data[1]>100) this.data[1]=100;
		data[2]= (float) (this.data[2] + ThreadLocalRandom.current().nextFloat()*2-1);
		if (this.data[2]<0) this.data[2]=0;	if (this.data[2]>100) this.data[2]=100;
		data[3]= (float) (this.data[3] + ThreadLocalRandom.current().nextFloat()*2-1);
		if (this.data[3]<0) this.data[3]=0;	if (this.data[3]>100) this.data[3]=100;
		//System.out.println("up "+data[0]+", "+data[1]+", "+data[2]+", "+data[3]);
		if(this.myMonitor!=null) {
			this.myMonitor.updateData(data);
		}
	}
	
	 public void run_slow() {
		 long lastcheck = System.currentTimeMillis();
		 while(this.machineOn) {
			 if(System.currentTimeMillis()-lastcheck>100) {
				 updateValues();
				 lastcheck=System.currentTimeMillis();
			 }
		 }
	 }
	 public void run() {
		 if(this.slowRun) { 
			 run_slow();
		}
		 else {
			 
			 while(this.machineOn) {
				 updateValues();
			 }
		}
	 }
	 
	 
	public static void main(String[] args) throws IOException {
		Monitor m= new Monitor();
		MeSystem mesys = new MeSystem();
		mesys.attachMonitor(m);
		mesys.slowRun = true;
		Thread subsystemUpdate = new Thread(mesys);
		subsystemUpdate.start();
	
	}

}
