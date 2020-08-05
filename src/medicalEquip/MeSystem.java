package medicalEquip;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

import monitor.Monitor;

public class MeSystem implements Runnable{
	int ALARMSNUMBER =4;
	private boolean machineOn;
	private double[] data;
	public Alarm[] alarmSet;
	private Monitor myMonitor=null;
	public boolean autoUpdate = true;
	public int dataLine=0;
	public MeSystem() throws IOException {
		this.machineOn=true;
		String row;
		final ClassLoader loader = MeSystem.class.getClassLoader();
		URL url = loader.getResource("confFiles/alarmsData.txt");
		BufferedReader csvReader = new BufferedReader(new FileReader(url.getPath()));
		int ALARMSNUMBER = Integer.valueOf(csvReader.readLine());
		this.data = new double[ALARMSNUMBER];
		this.alarmSet = new Alarm[ALARMSNUMBER];
		int i=0;
		while((row = csvReader.readLine()) != null) {
			String[] data =row.split(",");
			this.alarmSet[i]= new Alarm(Boolean.parseBoolean(data[0]),Integer.parseInt(data[1])*1000,"LOW", Integer.parseInt(data[2]),Integer.parseInt(data[3]), i,this);
			this.data[i]=ThreadLocalRandom.current().nextLong(Integer.parseInt(data[3]),Integer.parseInt(data[2]));
			i++;
		}		
		//this.data[1]=42;
		csvReader.close();
	}
	public MeSystem(String file) throws IOException {
		this.machineOn=true;
		String row;
		final ClassLoader loader = MeSystem.class.getClassLoader();
		URL url = loader.getResource(file);
		BufferedReader csvReader = new BufferedReader(new FileReader(url.getPath()));
		int ALARMSNUMBER = Integer.valueOf(csvReader.readLine());
		this.data = new double[ALARMSNUMBER];
		this.alarmSet = new Alarm[ALARMSNUMBER];
		int i=0;
		while((row = csvReader.readLine()) != null) {
			String[] data =row.split(",");
			this.alarmSet[i]= new Alarm(Boolean.parseBoolean(data[0]),Integer.parseInt(data[1])*1000,"LOW", Integer.parseInt(data[2]),Integer.parseInt(data[3]), i,this);
			this.data[i]=ThreadLocalRandom.current().nextLong(Integer.parseInt(data[3]),Integer.parseInt(data[2]));
			i++;
		}		
		this.data[1]=42;
		csvReader.close();
	}
	
	public double getValue(int ID) {
		return this.data[ID];
	}
	
	public void attachMonitor(Monitor m) {
		this.myMonitor=m;
	}
	
	
	/*
	 * necessari valori con rumore simil-perlin per garantire possibili allarmi prolungati
	 */
	private void updateValues() {
		if(this.autoUpdate ) {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i]+= ThreadLocalRandom.current().nextDouble()-0.5;
			if (this.data[i]<0) this.data[i]=0;	if (this.data[i]>100) this.data[i]=100;
		}}
		else {
			//READ FROM FILE or manual control
		}
	}
	
	public void changeValues(int sel,int value) {
		this.data[sel]+= value;
		if (this.data[sel]<0) this.data[sel]=0;	if (this.data[sel]>100) this.data[sel]=100;
	}
	
	public void setValues(int sel,int value) {
		if(value>100) {
			this.data[sel]=100;
		}
		else if (value<0) {
			this.data[sel]=0;	
		}
		else {
			this.data[sel]=value;	
		}
	}
	
	
	

	 public void run() {
		 long lastcheck = System.currentTimeMillis();
		 while(this.machineOn) {
			 if(System.currentTimeMillis()-lastcheck>100) {
				 updateValues();
				 lastcheck=System.currentTimeMillis();
				 this.myMonitor.updateData(this.data);
				 for(Alarm a: this.alarmSet) {
					 a.check();
				 }
			 }
		 }
	 }
	 
	 /*
	  * for testing only
	  */
	 public void runOnce() {
		 this.myMonitor.updateData(this.data);
		 for(Alarm a: this.alarmSet) {
			 a.check();
		 }
	 }
	 
	 
	public static void main(String[] args) throws IOException {
		MeSystem mesys = new MeSystem();
		Monitor m= new Monitor(mesys,mesys.alarmSet);
		mesys.attachMonitor(m);
		Thread subsystemUpdate = new Thread(mesys);
		subsystemUpdate.start();
	
	}

}
