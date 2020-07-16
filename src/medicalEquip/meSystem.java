package medicalEquip;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import monitor.Monitor;

public class MeSystem implements Runnable{
	int ALARMSNUMBER =4;
	private boolean machineOn;
	private double[] data = new double[ALARMSNUMBER];
	private Alarm[] alarmSet = new Alarm[ALARMSNUMBER];
	private boolean slowRun = false;
	private Monitor myMonitor=null;
	private String[] ranges= new String[ALARMSNUMBER];
	public MeSystem() throws IOException {
		this.machineOn=true;
		String row;
		URL url = getClass().getResource("alarmsData.txt");
		BufferedReader csvReader = new BufferedReader(new FileReader(url.getPath()));
		String s;
		for (int i = 0; i < alarmSet.length; i++) {
			row = csvReader.readLine();
			String[] data =row.split(",");
			this.alarmSet[i]= new Alarm(Boolean.parseBoolean(data[0]),Integer.parseInt(data[1])*1000,"LOW", Integer.parseInt(data[2]),Integer.parseInt(data[3]), i,this);
			s="max: "+Integer.parseInt(data[2])+"\nmin: "+Integer.parseInt(data[3]);
			this.ranges[i]=s;
		}
		this.data[0]=ThreadLocalRandom.current().nextLong(60,80);
		this.data[1]=ThreadLocalRandom.current().nextLong(10,12);
		this.data[2]=ThreadLocalRandom.current().nextLong(85,95);
		this.data[3]=ThreadLocalRandom.current().nextLong(15,20);
		
		csvReader.close();
	}
	
	private void startAlarms() {
		for(Alarm a: this.alarmSet) {
			a.attachMonitor(this.myMonitor);
		}
	}
	
	public double getValue(int ID) {
		return this.data[ID];
	}
	
	public void attachMonitor(Monitor m) {
		this.myMonitor=m;
		this.myMonitor.setRanges(this.ranges);
	}
	
	public void ackAlarm(int id) {
		this.alarmSet[id].acknowledged();
	}
	
	
	/*
	 * necessari valori con rumore simil-perlin per garantire possibili allarmi prolungati
	 */
	private void updateValues() {
		this.data[0] =  (this.data[0] + ThreadLocalRandom.current().nextDouble()-0.5);
		if (this.data[0]<0) this.data[0]=0;	if (this.data[0]>100) this.data[0]=100;
		data[1]=  (this.data[1] + ThreadLocalRandom.current().nextDouble()-0.5);
		if (this.data[1]<0) this.data[1]=0;	if (this.data[1]>100) this.data[1]=100;
		data[2]=  (this.data[2] + ThreadLocalRandom.current().nextDouble()-0.5);
		if (this.data[2]<0) this.data[2]=0;	if (this.data[2]>100) this.data[2]=100;
		data[3]=  (this.data[3] + ThreadLocalRandom.current().nextDouble()-0.5);
		if (this.data[3]<0) this.data[3]=0;	if (this.data[3]>100) this.data[3]=100;
		//System.out.println("up "+data[0]+", "+data[1]+", "+data[2]+", "+data[3]);
	}
	
	 public void run_slow() {
		 long lastcheck = System.currentTimeMillis();
		 while(this.machineOn) {
			 if(System.currentTimeMillis()-lastcheck>100) {
				 updateValues();
				 lastcheck=System.currentTimeMillis();
				 this.myMonitor.updateData(data);
				 for(Alarm a: this.alarmSet) {
					 a.check();
				 }
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
				 this.myMonitor.updateData(data);
			 }
		}
	 }
	 
	 
	public static void main(String[] args) throws IOException {
		MeSystem mesys = new MeSystem();
		Monitor m= new Monitor(mesys);
		mesys.attachMonitor(m);
		mesys.startAlarms();
		mesys.slowRun = true;
		Thread subsystemUpdate = new Thread(mesys);
		subsystemUpdate.start();
	
	}

}
