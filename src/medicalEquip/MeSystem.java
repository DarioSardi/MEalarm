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
	private double[] data;
	private Alarm[] alarmSet;
	private Monitor myMonitor=null;
	private String[] ranges;
	public MeSystem() throws IOException {
		this.machineOn=true;
		String row;
		URL url = getClass().getResource("alarmsData.txt");
		BufferedReader csvReader = new BufferedReader(new FileReader(url.getPath()));
		int ALARMSNUMBER = Integer.valueOf(csvReader.readLine());
		this.data = new double[ALARMSNUMBER];
		this.alarmSet = new Alarm[ALARMSNUMBER];
		this.ranges= new String[ALARMSNUMBER];
		String s;
		int i=0;
		while((row = csvReader.readLine()) != null) {
			String[] data =row.split(",");
			this.alarmSet[i]= new Alarm(Boolean.parseBoolean(data[0]),Integer.parseInt(data[1])*1000,"LOW", Integer.parseInt(data[2]),Integer.parseInt(data[3]), i,this);
			s="max: "+Integer.parseInt(data[2])+"<br/> min: "+Integer.parseInt(data[3]);
			this.ranges[i]=s;
			this.data[i]=ThreadLocalRandom.current().nextLong(Integer.parseInt(data[3]),Integer.parseInt(data[2]));
			i++;
		}		
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
	
	
	/*
	 * necessari valori con rumore simil-perlin per garantire possibili allarmi prolungati
	 */
	private void updateValues() {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i]+= ThreadLocalRandom.current().nextDouble()-0.5;
			if (this.data[i]<0) this.data[i]=0;	if (this.data[i]>100) this.data[i]=100;
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
	 
	 
	public static void main(String[] args) throws IOException {
		MeSystem mesys = new MeSystem();
		Monitor m= new Monitor(mesys,mesys.alarmSet);
		mesys.attachMonitor(m);
		mesys.startAlarms();
		Thread subsystemUpdate = new Thread(mesys);
		subsystemUpdate.start();
	
	}

}
