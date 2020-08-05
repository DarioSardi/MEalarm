package test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import medicalEquip.MeSystem;
import monitor.Monitor;

/*
 * not testing gui so using a monitor stub to simulate interactions, using a mock just for exercise.
 */
class MedicalEquipmentTest {
	
	
	
	@Nested
	class NonLatchingTest{ 
		MeSystem mesys;
		Monitor m;
		@BeforeEach 
		void setup() throws IOException {
			this.mesys = new MeSystem("confFiles/alarmsDataTest.txt");
			//false,2,20,5,0
			//true,2,40,10,1

			this.m= new MonitorStub(this.mesys,this.mesys.alarmSet);
			this.mesys.attachMonitor(this.m);
			this.mesys.autoUpdate = false;
			this.mesys.runOnce();
		}
		@Test 
		void Latching() throws IOException {
			assertFalse(this.mesys.alarmSet[0].isLatching());
		}
		
		@Test 
		void noAlarmCond() {
			this.mesys.setValues(0,19);
			assertFalse(this.mesys.alarmSet[0].isAlarmCondition());
		}
		@Test 
		void alarmCond() throws InterruptedException {
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isAlarmCondition());
			//Alarm not rising
			this.mesys.setValues(0,19);
			this.mesys.runOnce();
			assertFalse(this.mesys.alarmSet[0].isAlarmCondition());
		
		}
		
		
		@Test 
		void alarmTriggered() throws InterruptedException {
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Alarm triggered test
			this.mesys.setValues(0,25);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertTrue(this.mesys.alarmSet[0].isAudioAlarm());
			//Alarm reset
			this.mesys.setValues(0,19);
			this.mesys.runOnce();
			Thread.sleep(3000);
			this.mesys.runOnce();
			assertFalse(this.mesys.alarmSet[0].isAlarmCondition());
		}
		@Test
		void testAck() throws InterruptedException{
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Alarm triggered test
			this.mesys.setValues(0,25);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertTrue(this.mesys.alarmSet[0].isAudioAlarm());
			this.mesys.alarmSet[0].acknowledged();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertFalse(this.mesys.alarmSet[0].isAudioAlarm());
			//expiring the ack
			Thread.sleep(11000);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertTrue(this.mesys.alarmSet[0].isAudioAlarm());
		}
		@Test
		void testReset() {
			assertFalse(this.mesys.alarmSet[0].alarmReset());
		}
		
		@Test
		void deactivateAlarm() throws InterruptedException {
			//OFF
			this.mesys.alarmSet[0].alarmInactivationStateSwitch(false);
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Alarm should not be triggered 
			this.mesys.setValues(0,25);
			this.mesys.runOnce();
			assertFalse(this.mesys.alarmSet[0].isVisualAlarm());
			assertFalse(this.mesys.alarmSet[0].isAudioAlarm());
			//ON
			this.mesys.alarmSet[0].alarmInactivationStateSwitch(true);
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Alarm should trigger
			this.mesys.setValues(0,25);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertTrue(this.mesys.alarmSet[0].isAudioAlarm());
		}
		
		
		@Test
		void audioSettings() throws InterruptedException {
			//DISABLE AUDIO
			this.mesys.alarmSet[0].disableAudio();
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Alarm should trigger
			this.mesys.setValues(0,25);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertFalse(this.mesys.alarmSet[0].isAudioAlarm());
			//ENABLE AUDIO
			this.mesys.alarmSet[0].enableAudio();
			this.mesys.setValues(0,21);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Alarm should trigger
			this.mesys.setValues(0,25);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[0].isVisualAlarm());
			assertTrue(this.mesys.alarmSet[0].isAudioAlarm());
		}
	}
	
	@Nested
	class LatchingTest{ 
		MeSystem mesys;
		Monitor m;
		@BeforeEach 
		void setup() throws IOException {			
			this.mesys = new MeSystem("confFiles/alarmsDataTest.txt");
			//false,2,20,5,0
			//true,2,40,10,1
			this.m= new MonitorStub(this.mesys,this.mesys.alarmSet);
			this.mesys.attachMonitor(this.m);
			this.mesys.autoUpdate = false;
			this.mesys.runOnce();
		}
		@Test 
		void Latching() throws IOException {
			assertTrue(this.mesys.alarmSet[1].isLatching());
		}
		
		@Test 
		void latchingReset() throws InterruptedException {
			this.mesys.setValues(1,5);
			this.mesys.runOnce();
			Thread.sleep(3000);
			this.mesys.runOnce();
			Thread.sleep(3000);
			this.mesys.setValues(1,20);
			this.mesys.runOnce();
			Thread.sleep(3000);
			//Waiting reset
			assertTrue(this.mesys.alarmSet[1].isWaitingReset());
			this.mesys.alarmSet[1].alarmReset();
			assertFalse(this.mesys.alarmSet[1].isAlarmCondition());
			assertFalse(this.mesys.alarmSet[1].isWaitingReset());
		}
		
		@Test 
		void alarmWileWaitingReset() throws InterruptedException {
			this.mesys.setValues(1,5);
			this.mesys.runOnce();
			Thread.sleep(3000);
			this.mesys.runOnce();
			Thread.sleep(3000);
			this.mesys.setValues(1,20);
			this.mesys.runOnce();
			//Waiting reset
			this.mesys.setValues(1,5);
			this.mesys.runOnce();
			assertTrue(this.mesys.alarmSet[1].isAlarmCondition());
			Thread.sleep(3000);
		}
		
	}
	
	

}
