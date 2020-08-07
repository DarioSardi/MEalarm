asm MeAlarm

import StandardLibrary
signature:
	enum domain AlarmStatus = {OFF | ALARMCONDITION | ON_LATCHING | WAITING_RESET | ON | DISABLED }
	enum domain AudioStatus = {AUDIOON | AUDIOOFF}
	domain Percentage subsetof Integer
	static minValue : Percentage
	static maxValue : Percentage
	static delayedStart : Percentage
	static isLatching : Boolean
	dynamic controlled value : Percentage
	dynamic controlled alarm : AlarmStatus
	dynamic controlled audio : AudioStatus
	//dynamic controlled output: Any	
	dynamic monitored newValue: Percentage
	dynamic controlled timeAlCondition : Percentage
	dynamic monitored changeAudioStatusTo : Boolean
	dynamic controlled audioActive : Boolean
	dynamic controlled waitingReset : Boolean

definitions:
	domain Percentage = {0:100} //smv
	//domain Percentage = {0:100}
	
	function minValue = 5
	function maxValue = 10
	function delayedStart = 3
	function isLatching = false


	rule r_Status =
		switch (value<=maxValue and value>=minValue) //OK cond
			case true:
				//waiting reset
				par
					alarm := OFF
					audio := AUDIOOFF
				endpar
				
			case false:
				switch timeAlCondition>delayedStart
					case false:
						par
							alarm := ALARMCONDITION
							audio := AUDIOOFF
							timeAlCondition:=timeAlCondition+1
						endpar
					case true:
						seq
						audioActive := changeAudioStatusTo
						switch isLatching
							case true://LATCHING
								switch audioActive
									case true:
										par
											alarm := ON_LATCHING
											audio := AUDIOON
										endpar
									case false:
										par
											alarm := ON_LATCHING
											audio := AUDIOOFF
										endpar
								endswitch
							case false://NON LATCHING
								switch audioActive
									case true:
										par
											alarm := ON
											audio := AUDIOON
										endpar
									case false:
										par
											alarm := ON
											audio := AUDIOOFF
										endpar
								endswitch
						endswitch
						endseq
				endswitch
					
		endswitch

main rule r_Main =  
	seq
		r_Status[]
		value := newValue
	endseq
		
default init s0:
	function value = 6
	function alarm = OFF
	function timeAlCondition = 0
	function audioActive = true
	function waitingReset = false